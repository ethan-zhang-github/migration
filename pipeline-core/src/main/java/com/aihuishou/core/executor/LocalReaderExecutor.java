package com.aihuishou.core.executor;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import com.aihuishou.core.annotation.TaskConfigAttributes;
import com.aihuishou.core.buffer.DataBuffer;
import com.aihuishou.core.context.DataChunk;
import com.aihuishou.core.context.TaskContext;
import com.aihuishou.core.context.TaskState;
import com.aihuishou.core.event.TaskFailedEvent;
import com.aihuishou.core.event.TaskWarnningEvent;
import com.aihuishou.core.exception.TaskExecutionException;
import com.aihuishou.core.reader.PipeReader;
import com.aihuishou.core.task.PipeTask;

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
        if (context.getReaderState().canRun()) {
            context.setReaderState(TaskState.RUNNING);
        } else {
            throw new TaskExecutionException("The reader can not run on this state!");
        }
        TaskConfigAttributes attributes = TaskConfigAttributes.fromClass(reader.getClass());
        Retryer<Boolean> retryer = attributes.buidlRetryer();
        DataBuffer<I> readBuffer = context.getReadBuffer();
        readerFuture =  executor.submit(() -> {
            reader.initialize(context);
            while (context.getReaderState() == TaskState.RUNNING) {
                if (Thread.currentThread().isInterrupted()) {
                    context.setReaderState(TaskState.TERMINATED);
                    return;
                }
                DataChunk<I> chunk;
                try {
                    chunk = reader.read(context);
                } catch (Exception e) {
                    Set<Class<? extends Throwable>> interruptFor = attributes.getInterruptFor();
                    if (interruptFor.stream().anyMatch(t -> t.isAssignableFrom(e.getClass()))) {
                        context.setReaderState(TaskState.FAILED);
                        reader.destroy(context);
                        task.getDispatcher().dispatch(new TaskFailedEvent(task, TaskFailedEvent.Cause.READER_FAILED, e));
                        return;
                    } else {
                        task.getDispatcher().dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.READER_FAILED, e));
                        continue;
                    }
                }
                if (chunk.isEmpty()) {
                    context.setReaderState(TaskState.TERMINATED);
                    reader.destroy(context);
                    return;
                } else {
                    for (I i : chunk) {
                        try {
                            if (retryer.call(() -> readBuffer.tryProduce(i))) {
                                context.incrReadCount(1);
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
        if (context.getReaderState().canStop()) {
            context.setReaderState(TaskState.STOPPING);
        } else {
            throw new TaskExecutionException("The reader can not stop on this state!");
        }
    }

    @Override
    public void shutDown(PipeTask<I, O> task, PipeReader<I> reader) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().canShutdown()) {
            context.setReaderState(TaskState.TERMINATED);
            Optional.ofNullable(readerFuture).ifPresent(t -> t.cancel(true));
        } else {
            throw new TaskExecutionException("The reader can not shutdown on this state!");
        }
    }

}
