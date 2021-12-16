package com.aihuishou.core.executor;

import com.aihuishou.core.annotation.TaskConfigAttributes;
import com.aihuishou.core.buffer.DataBuffer;
import com.aihuishou.core.context.DataChunk;
import com.aihuishou.core.context.TaskContext;
import com.aihuishou.core.context.TaskState;
import com.aihuishou.core.event.TaskFailedEvent;
import com.aihuishou.core.event.TaskWarnningEvent;
import com.aihuishou.core.event.dispatcher.TaskEventDispatcher;
import com.aihuishou.core.processor.PipeProcessor;
import com.aihuishou.core.task.PipeTask;
import com.aihuishou.core.utils.ThreadUtil;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import com.aihuishou.core.exception.TaskExecutionException;
import com.aihuishou.core.processor.PipeProcessorChain;
import com.aihuishou.core.processor.PipeProcessorNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@Getter
public class LocalProcessorExecutor<I, O> implements ProcessorExecutor<I, O> {

    private final ListeningExecutorService executor;

    private List<ListenableFuture<?>> processorFutures = Collections.emptyList();

    public LocalProcessorExecutor(ExecutorService executor) {
        this.executor = MoreExecutors.listeningDecorator(executor);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void start(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain) {
        TaskContext<I, O> context = task.getContext();
        if (context.getProcessorState().canRun()) {
            context.setProcessorState(TaskState.RUNNING);
        } else {
            throw new TaskExecutionException("The processor can not run on this state!");
        }
        TaskEventDispatcher dispatcher = task.getDispatcher();
        processorFutures = new ArrayList<>(processorChain.length());
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
            cur.setState(TaskState.RUNNING);
            processorFutures.add(executor.submit(() -> {
                while (true) {
                    // 上个节点状态，头节点取 reader state，非头节点取上个节点的 state
                    TaskState preState = isHead ? context.getReaderState() : pre.getState();
                    // 若上个节点的状态不是 running 并且读取缓冲区为空，跳出循环
                    if (preState != TaskState.RUNNING && readBuffer.isEmpty()) {
                        break;
                    }
                    // 若当前线程中断，则将当前节点及 processor 状态设为 terminated，任务终止
                    if (ThreadUtil.isCurThreadInterrupted()) {
                        cur.setState(TaskState.TERMINATED);
                        context.setProcessorState(TaskState.TERMINATED);
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
                            cur.setState(TaskState.FAILED);
                            context.setProcessorState(TaskState.FAILED);
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
                                    context.incrProcessedCount(1);
                                }
                            } catch (ExecutionException | RetryException e) {
                                dispatcher.dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.PROCESSOR_TO_BUFFER_FAILED, e));
                            }
                        });
                    }
                }
                // 将当前节点状态更新为上个节点状态
                TaskState preState = isHead ? context.getReaderState() : pre.getState();
                cur.setState(preState);
                if (isTail) {
                    // 若当前节点为尾结点，则将 processor 状态更新为与 reader 一致
                    context.setProcessorState(context.getReaderState());
                }
            }));
        }
    }

    @Override
    public void stop(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain) {
        TaskContext<I, O> context = task.getContext();
        if (context.getProcessorState().canStop()) {
            context.setProcessorState(TaskState.STOPPING);
            processorChain.getNodes().forEach(node -> node.setState(TaskState.STOPPING));
        } else {
            throw new TaskExecutionException("The processor can not stop on this state!");
        }
    }

    @Override
    public void shutDown(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain) {
        TaskContext<I, O> context = task.getContext();
        if (context.getProcessorState().canShutdown()) {
            context.setProcessorState(TaskState.TERMINATED);
            processorChain.getNodes().forEach(node -> node.setState(TaskState.TERMINATED));
            processorFutures.forEach(future -> future.cancel(true));
        } else {
            throw new TaskExecutionException("The processor can not shutdown on this state!");
        }
    }

}
