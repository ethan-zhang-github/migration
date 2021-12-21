package com.aihuishou.pipeline.core.executor;

import com.aihuishou.pipeline.core.annotation.TaskConfigAttributes;
import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.context.TaskState;
import com.aihuishou.pipeline.core.event.TaskFailedEvent;
import com.aihuishou.pipeline.core.event.TaskWarnningEvent;
import com.aihuishou.pipeline.core.exception.TaskExecutionException;
import com.aihuishou.pipeline.core.reader.PipeReader;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * 本地读取执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
@Getter
public class LocalReaderExecutor<I, O> implements ReaderExecutor<I, O> {

    private final ListeningExecutorService executor;

    private ListenableFuture<?> readerFuture;

    public LocalReaderExecutor(ExecutorService executor) {
        this.executor = MoreExecutors.listeningDecorator(executor);
    }

    @Override
    public void start(PipeTask<I, O> task, PipeReader<I> reader) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().get().canRun()) {
            context.getReaderState().set(TaskState.RUNNING);
        } else {
            throw new TaskExecutionException("The reader can not run on this state!");
        }
        TaskConfigAttributes attributes = TaskConfigAttributes.fromClass(reader.getClass());
        Retryer<Boolean> retryer = attributes.buidlRetryer();
        DataBuffer<I> readBuffer = context.getReadBuffer();
        readerFuture =  executor.submit(() -> {
            reader.initialize(context);
            while (context.getReaderState().get() == TaskState.RUNNING) {
                if (Thread.currentThread().isInterrupted()) {
                    context.getReaderState().set(TaskState.TERMINATED);
                    return;
                }
                DataChunk<I> chunk;
                try {
                    chunk = reader.read(context);
                } catch (Exception e) {
                    Set<Class<? extends Throwable>> interruptFor = attributes.getInterruptFor();
                    if (interruptFor.stream().anyMatch(t -> t.isAssignableFrom(e.getClass()))) {
                        context.getReaderState().set(TaskState.FAILED);
                        reader.destroy(context);
                        task.getDispatcher().dispatch(new TaskFailedEvent(task, TaskFailedEvent.Cause.READER_FAILED, e));
                        return;
                    } else {
                        task.getDispatcher().dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.READER_FAILED, e));
                        continue;
                    }
                }
                if (chunk.isEmpty()) {
                    context.getReaderState().set(TaskState.TERMINATED);
                    reader.destroy(context);
                    return;
                } else {
                    for (I i : chunk) {
                        try {
                            if (retryer.call(() -> readBuffer.tryProduce(i))) {
                                context.getReadCounter().incr();
                            }
                        } catch (ExecutionException | RetryException e) {
                            task.getDispatcher().dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.READER_TO_BUFFER_FAILED, e));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void stop(PipeTask<I, O> task, PipeReader<I> reader) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().get().canStop()) {
            context.getReaderState().set(TaskState.STOPPING);
        } else {
            throw new TaskExecutionException("The reader can not stop on this state!");
        }
    }

    @Override
    public void shutDown(PipeTask<I, O> task, PipeReader<I> reader) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().get().canShutdown()) {
            context.getReaderState().set(TaskState.TERMINATED);
            Optional.ofNullable(readerFuture).ifPresent(t -> t.cancel(true));
        } else {
            throw new TaskExecutionException("The reader can not shutdown on this state!");
        }
    }

    @Override
    public void join(PipeTask<I, O> task, PipeReader<I> reader) {
        try {
            readerFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new TaskExecutionException("The reader join failed!", e);
        }
    }

}
