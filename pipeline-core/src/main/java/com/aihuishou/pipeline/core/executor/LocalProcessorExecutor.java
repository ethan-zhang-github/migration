package com.aihuishou.pipeline.core.executor;

import com.aihuishou.pipeline.core.annotation.TaskConfigAttributes;
import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.context.TaskState;
import com.aihuishou.pipeline.core.event.TaskFailedEvent;
import com.aihuishou.pipeline.core.event.TaskWarnningEvent;
import com.aihuishou.pipeline.core.event.dispatcher.TaskEventDispatcher;
import com.aihuishou.pipeline.core.exception.TaskExecutionException;
import com.aihuishou.pipeline.core.processor.PipeProcessor;
import com.aihuishou.pipeline.core.processor.PipeProcessorChain;
import com.aihuishou.pipeline.core.processor.PipeProcessorNode;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.aihuishou.pipeline.core.utils.BatchUtil;
import com.aihuishou.pipeline.core.utils.Functions;
import com.aihuishou.pipeline.core.utils.ThreadUtil;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Getter
public class LocalProcessorExecutor<I, O> implements ProcessorExecutor<I, O> {

    private final Executor executor;

    private CompletableFuture<Void> future;

    public LocalProcessorExecutor(Executor executor) {
        this.executor = executor;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void start(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain) {
        TaskContext<I, O> context = task.getContext();
        if (context.getProcessorState().get().canRun()) {
            context.getProcessorState().set(TaskState.RUNNING);
        } else {
            throw new TaskExecutionException("The processor can not run on this state!");
        }
        TaskEventDispatcher dispatcher = task.getDispatcher();
        List<CompletableFuture<Void>> futures = new ArrayList<>(processorChain.length());
        LinkedList<PipeProcessorNode<?, ?>> nodes = processorChain.getNodes();
        // 遍历执行 processor
        for (int i = 0; i < nodes.size(); i++) {
            // 当前节点是否为头节点
            boolean isHead = i == 0;
            // 当前节点是否为尾结点
            boolean isTail = i == nodes.size() - 1;
            // 当前节点
            PipeProcessorNode cur = nodes.get(i);
            PipeProcessor processor = cur.getProcessor();
            TaskConfigAttributes attributes = TaskConfigAttributes.fromClass(processor.getClass());
            Retryer<Boolean> retryer = attributes.buidlRetryer();
            // 上个节点
            PipeProcessorNode pre = isHead ? null : nodes.get(i - 1);
            // 读取缓冲区，头节点取 reader buffer，非头节点取上个节点的 buffer
            DataBuffer readBuffer = isHead ? context.getReadBuffer() : pre.getBuffer();
            // 写出缓冲区，尾结点取 wirter buffer，非尾结点取当前节点的 buffer
            DataBuffer writeBuffer = isTail ? context.getWriteBuffer() : cur.getBuffer();
            // 设置当前节点状态为运行中
            cur.getState().set(TaskState.RUNNING);
            futures.add(CompletableFuture.runAsync(() -> {
                while (true) {
                    // 上个节点状态，头节点取 reader state，非头节点取上个节点的 state
                    TaskState preState = isHead ? context.getReaderState().get() : (TaskState) pre.getState().get();
                    // 若上个节点的状态不是 running 并且读取缓冲区为空，跳出循环
                    if (preState != TaskState.RUNNING && readBuffer.isEmpty()) {
                        break;
                    }
                    // 若当前线程中断，则将当前节点及 processor 状态设为 terminated，任务终止
                    if (ThreadUtil.isCurThreadInterrupted()) {
                        cur.getState().set(TaskState.TERMINATED);
                        context.getProcessorState().set(TaskState.TERMINATED);
                        return;
                    }
                    DataChunk output;
                    try {
                        List input = readBuffer.consumeIfPossible(attributes.getMaxConsumeCount());
                        if (CollectionUtils.isEmpty(input)) {
                            Thread.yield();
                            continue;
                        } else {
                            output = processor.process(context, DataChunk.of(input));
                        }
                    } catch (Exception e) {
                        if (attributes.shouldInterruptFor(e)) {
                            // 若当前异常需要中断任务，则将当前节点及 processor 状态设为 failed，发布任务失败事件
                            cur.getState().set(TaskState.FAILED);
                            context.getProcessorState().set(TaskState.FAILED);
                            dispatcher.dispatch(new TaskFailedEvent(task, TaskFailedEvent.Cause.PROCESSOR_FAILED, e));
                            return;
                        } else {
                            // 若当前异常不需要中断任务，则发布任务警告事件，继续执行任务
                            dispatcher.dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.PROCESSOR_FAILED, e));
                            continue;
                        }
                    }
                    if (output.isNotEmpty()) {
                        output.forEach(o -> {
                            try {
                                // 尝试将数据写入缓冲区，若当前节点为尾结点，则更新 processor 进度
                                if (retryer.call(() -> writeBuffer.tryProduce(o)) && isTail) {
                                    context.getProcessedCounter().incr();
                                }
                            } catch (ExecutionException | RetryException e) {
                                dispatcher.dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.PROCESSOR_TO_BUFFER_FAILED, e));
                            }
                        });
                    }
                }
                // 将当前节点状态更新为上个节点状态
                TaskState preState = isHead ? context.getReaderState().get() : (TaskState) pre.getState().get();
                cur.getState().set(preState);
                if (isTail) {
                    // 若当前节点为尾结点，则将 processor 状态更新为与 reader 一致
                    context.getProcessorState().set(context.getReaderState());
                }
            }, executor));
        }
        future = BatchUtil.merge(futures, Functions.firstOneBinaryOperator());
    }

    @Override
    public void stop(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain) {
        TaskContext<I, O> context = task.getContext();
        if (context.getProcessorState().get().canStop()) {
            context.getProcessorState().set(TaskState.STOPPING);
            processorChain.getNodes().forEach(node -> node.getState().set(TaskState.STOPPING));
        } else {
            throw new TaskExecutionException("The processor can not stop on this state!");
        }
    }

    @Override
    public void shutDown(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain) {
        TaskContext<I, O> context = task.getContext();
        if (context.getProcessorState().get().canShutdown()) {
            context.getProcessorState().set(TaskState.TERMINATED);
            processorChain.getNodes().forEach(node -> node.getState().set(TaskState.TERMINATED));
            Optional.ofNullable(future).ifPresent(f -> f.cancel(true));
        } else {
            throw new TaskExecutionException("The processor can not shutdown on this state!");
        }
    }

    @SuppressWarnings("all")
    @Override
    public void join(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain) {
        future.join();
    }

}
