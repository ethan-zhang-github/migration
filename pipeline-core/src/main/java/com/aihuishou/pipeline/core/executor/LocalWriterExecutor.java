package com.aihuishou.pipeline.core.executor;

import com.aihuishou.pipeline.core.annotation.TaskConfigAttributes;
import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.context.TaskState;
import com.aihuishou.pipeline.core.event.TaskFailedEvent;
import com.aihuishou.pipeline.core.event.TaskFinishedEvent;
import com.aihuishou.pipeline.core.event.TaskWarnningEvent;
import com.aihuishou.pipeline.core.exception.TaskExecutionException;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.aihuishou.pipeline.core.writer.PipeWriter;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 本地写入执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
@Getter
public class LocalWriterExecutor<I, O> implements WriterExecutor<I, O> {

    private final Executor executor;

    private CompletableFuture<Void> future;

    public LocalWriterExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void start(PipeTask<I, O> task, PipeWriter<O> writer) {
        TaskContext<I, O> context = task.getContext();
        if (context.getWriterState().get().canRun()) {
            context.getWriterState().set(TaskState.RUNNING);
        } else {
            throw new TaskExecutionException("The writer can not run on this state!");
        }
        DataBuffer<O> writeBuffer = context.getWriteBuffer();
        TaskConfigAttributes attributes = TaskConfigAttributes.fromClass(writer.getClass());
        future = CompletableFuture.runAsync(() -> {
            writer.initialize(context);
            while (context.getProcessorState().get() == TaskState.RUNNING || !writeBuffer.isEmpty()) {
                if (Thread.currentThread().isInterrupted()) {
                    context.getWriterState().set(TaskState.TERMINATED);
                    return;
                }
                try {
                    List<O> output = writeBuffer.consumeIfPossible(attributes.getMaxConsumeCount());
                    if (CollectionUtils.isEmpty(output)) {
                        Thread.yield();
                    } else {
                        context.getWriterCounter().incr(writer.write(context, DataChunk.of(output)));
                    }
                } catch (Exception e) {
                    if (attributes.shouldInterruptFor(e)) {
                        context.getWriterState().set(TaskState.FAILED);
                        writer.destroy(context);
                        task.getDispatcher().dispatch(new TaskFailedEvent(task, TaskFailedEvent.Cause.WRITER_FAILED, e));
                        return;
                    } else {
                        task.getDispatcher().dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.WRITER_FAILED, e));
                    }
                }
            }
            context.getWriterState().set(context.getProcessorState());
            if (context.getWriterState().get() == TaskState.TERMINATED || context.getWriterState().get() == TaskState.FAILED) {
                writer.destroy(context);
            }
            if (context.isTerminated()) {
                task.getDispatcher().dispatch(new TaskFinishedEvent(task));
            }
        }, executor);
    }

    @Override
    public void stop(PipeTask<I, O> task, PipeWriter<O> writer) {
        TaskContext<I, O> context = task.getContext();
        if (context.getWriterState().get().canStop()) {
            context.getWriterState().set(TaskState.STOPPING);
        } else {
            throw new TaskExecutionException("The writer can not stop on this state!");
        }
    }

    @Override
    public void shutDown(PipeTask<I, O> task, PipeWriter<O> writer) {
        TaskContext<I, O> context = task.getContext();
        if (context.getWriterState().get().canShutdown()) {
            context.getWriterState().set(TaskState.TERMINATED);
            Optional.ofNullable(future).ifPresent(f -> f.cancel(true));
        } else {
            throw new TaskExecutionException("The writer can not shutdown on this state!");
        }
    }

    @Override
    public void join(PipeTask<I, O> task, PipeWriter<O> writer) {
        future.join();
    }

}
