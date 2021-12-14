package priv.ethanzhang.pipeline.core.executor;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.pipeline.core.annotation.TaskConfigAttributes;
import priv.ethanzhang.pipeline.core.buffer.DataBuffer;
import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskContext;
import priv.ethanzhang.pipeline.core.context.TaskState;
import priv.ethanzhang.pipeline.core.event.TaskFailedEvent;
import priv.ethanzhang.pipeline.core.event.TaskFinishedEvent;
import priv.ethanzhang.pipeline.core.event.TaskWarnningEvent;
import priv.ethanzhang.pipeline.core.exception.TaskExecutionException;
import priv.ethanzhang.pipeline.core.task.PipeTask;
import priv.ethanzhang.pipeline.core.writer.PipeWriter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * 本地写入执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
@Getter
public class LocalWriterExecutor<I, O> implements WriterExecutor<I, O> {

    private final ListeningExecutorService executor;

    private ListenableFuture<?> writerFuture;

    public LocalWriterExecutor(ExecutorService executor) {
        this.executor = MoreExecutors.listeningDecorator(executor);
    }

    @Override
    public void start(PipeTask<I, O> task, PipeWriter<O> writer) {
        TaskContext<I, O> context = task.getContext();
        if (context.getWriterState().canRun()) {
            context.setWriterState(TaskState.RUNNING);
        } else {
            throw new TaskExecutionException("The writer can not run on this state!");
        }
        DataBuffer<O> writeBuffer = context.getWriteBuffer();
        TaskConfigAttributes attributes = TaskConfigAttributes.fromClass(writer.getClass());
        writerFuture = executor.submit(() -> {
            writer.initialize(context);
            while (context.getProcessorState() == TaskState.RUNNING || !writeBuffer.isEmpty()) {
                if (Thread.currentThread().isInterrupted()) {
                    context.setWriterState(TaskState.TERMINATED);
                    return;
                }
                try {
                    List<O> output = writeBuffer.consumeIfPossible(attributes.getMaxConsumeCount());
                    if (CollectionUtils.isEmpty(output)) {
                        Thread.yield();
                    } else {
                        context.incrWrittenCount(writer.write(context, DataChunk.of(output)));
                    }
                } catch (Exception e) {
                    if (attributes.shouldInterruptFor(e)) {
                        context.setWriterState(TaskState.FAILED);
                        writer.destroy(context);
                        task.getDispatcher().dispatch(new TaskFailedEvent(task, TaskFailedEvent.Cause.WRITER_FAILED, e));
                        return;
                    } else {
                        task.getDispatcher().dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.WRITER_FAILED, e));
                    }
                }
            }
            context.setWriterState(context.getProcessorState());
            if (context.getWriterState() == TaskState.TERMINATED || context.getWriterState() == TaskState.FAILED) {
                writer.destroy(context);
            }
            if (context.isTerminated()) {
                task.getDispatcher().dispatch(new TaskFinishedEvent(task));
            }
        });
    }

    @Override
    public void stop(PipeTask<I, O> task, PipeWriter<O> writer) {
        TaskContext<I, O> context = task.getContext();
        if (context.getWriterState().canStop()) {
            context.setWriterState(TaskState.STOPPING);
        } else {
            throw new TaskExecutionException("The writer can not stop on this state!");
        }
    }

    @Override
    public void shutDown(PipeTask<I, O> task, PipeWriter<O> writer) {
        TaskContext<I, O> context = task.getContext();
        if (context.getWriterState().canShutdown()) {
            context.setWriterState(TaskState.TERMINATED);
            Optional.ofNullable(writerFuture).ifPresent(t -> t.cancel(true));
        } else {
            throw new TaskExecutionException("The writer can not shutdown on this state!");
        }
    }

}
