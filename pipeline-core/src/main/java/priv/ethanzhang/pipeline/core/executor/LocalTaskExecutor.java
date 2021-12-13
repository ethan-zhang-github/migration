package priv.ethanzhang.pipeline.core.executor;

import com.github.rholder.retry.*;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.pipeline.core.buffer.DataBuffer;
import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskState;
import priv.ethanzhang.pipeline.core.event.TaskFailedEvent;
import priv.ethanzhang.pipeline.core.event.TaskFinishedEvent;
import priv.ethanzhang.pipeline.core.event.TaskWarnningEvent;
import priv.ethanzhang.pipeline.core.processor.PipeProcessor;
import priv.ethanzhang.pipeline.core.reader.PipeReader;
import priv.ethanzhang.pipeline.core.task.PipeTask;
import priv.ethanzhang.pipeline.core.writer.PipeWriter;
import priv.ethanzhang.pipeline.core.annotation.TaskConfigAttributes;
import priv.ethanzhang.pipeline.core.context.TaskContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 本地任务执行器
 */
@Getter
public class LocalTaskExecutor<I, O> extends AbstractTaskExecutor<I, O> {

    private final ListeningExecutorService executor;

    private ListenableFuture<?> readerTask;

    private ListenableFuture<?> processorTask;

    private ListenableFuture<?> writerTask;

    public LocalTaskExecutor(ExecutorService executor) {
        this.executor = MoreExecutors.listeningDecorator(executor);
    }

    @Override
    protected void startReader(PipeTask<I, O> task) {
        readerTask =  executor.submit(() -> {
            TaskContext<I, O> context = task.getContext();
            PipeReader<I> reader = task.getReader();
            DataBuffer<I> readBuffer = context.getReadBuffer();
            TaskConfigAttributes attributes = TaskConfigAttributes.fromClass(reader.getClass());
            Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                    .retryIfResult(Boolean.FALSE::equals)
                    .withStopStrategy(StopStrategies.stopAfterAttempt(attributes.getMaxProduceRetryTimes()))
                    .withWaitStrategy(WaitStrategies.fixedWait(attributes.getProduceRetryPeriodSeconds(), TimeUnit.SECONDS))
                    .build();
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
    protected void startProcessor(PipeTask<I, O> task) {
        processorTask = executor.submit(() -> {
            TaskContext<I, O> context = task.getContext();
            PipeProcessor<I, O> processor = task.getProcessor();
            DataBuffer<I> readBuffer = context.getReadBuffer();
            DataBuffer<O> writeBuffer = context.getWriteBuffer();
            TaskConfigAttributes attributes = TaskConfigAttributes.fromClass(processor.getClass());
            Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                    .retryIfResult(Boolean.FALSE::equals)
                    .withStopStrategy(StopStrategies.stopAfterAttempt(attributes.getMaxProduceRetryTimes()))
                    .withWaitStrategy(WaitStrategies.fixedWait(attributes.getProduceRetryPeriodSeconds(), TimeUnit.SECONDS))
                    .build();
            while (context.getReaderState() == TaskState.RUNNING || !readBuffer.isEmpty()) {
                if (Thread.currentThread().isInterrupted()) {
                    context.setProcessorState(TaskState.TERMINATED);
                    return;
                }
                DataChunk<O> output;
                try {
                    List<I> input = readBuffer.consumeIfPossible(attributes.getMaxConsumeCount());
                    if (CollectionUtils.isEmpty(input)) {
                        Thread.yield();
                        continue;
                    } else {
                        output = processor.process(context, DataChunk.of(input));
                    }
                } catch (Exception e) {
                    if (attributes.shouldInterruptFor(e)) {
                        context.setProcessorState(TaskState.FAILED);
                        task.getDispatcher().dispatch(new TaskFailedEvent(task, TaskFailedEvent.Cause.PROCESSOR_FAILED, e));
                        return;
                    } else {
                        task.getDispatcher().dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.PROCESSOR_FAILED, e));
                        continue;
                    }
                }
                if (output.isNotEmpty()) {
                    for (O o : output) {
                        try {
                            if (retryer.call(() -> writeBuffer.tryProduce(o))) {
                                context.incrProcessedCount(1);
                            }
                        } catch (ExecutionException | RetryException e) {
                            task.getDispatcher().dispatch(new TaskWarnningEvent(task, TaskWarnningEvent.Cause.PROCESSOR_TO_BUFFER_FAILED, e));
                        }
                    }
                }
            }
            context.setProcessorState(context.getReaderState());
        });
    }

    @Override
    protected void startWriter(PipeTask<I, O> task) {
        writerTask = executor.submit(() -> {
            TaskContext<I, O> context = task.getContext();
            PipeWriter<O> writer = task.getWriter();
            DataBuffer<O> writeBuffer = context.getWriteBuffer();
            TaskConfigAttributes attributes = TaskConfigAttributes.fromClass(writer.getClass());
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
    protected void shutdownReader(PipeTask<I, O> task) {
        Optional.ofNullable(readerTask).ifPresent(t -> t.cancel(true));
    }

    @Override
    protected void shutdownProcessor(PipeTask<I, O> task) {
        Optional.ofNullable(processorTask).ifPresent(t -> t.cancel(true));
    }

    @Override
    protected void shutdownWriter(PipeTask<I, O> task) {
        Optional.ofNullable(writerTask).ifPresent(t -> t.cancel(true));
    }

}
