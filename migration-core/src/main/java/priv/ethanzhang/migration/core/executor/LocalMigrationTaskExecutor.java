package priv.ethanzhang.migration.core.executor;

import com.github.rholder.retry.*;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.migration.core.annotation.MigrationConfigAttributes;
import priv.ethanzhang.migration.core.buffer.DataBuffer;
import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.event.TaskTaskFailedEvent;
import priv.ethanzhang.migration.core.event.TaskTaskFinishedEvent;
import priv.ethanzhang.migration.core.event.TaskTaskWarnningEvent;
import priv.ethanzhang.migration.core.processor.MigrationProcessor;
import priv.ethanzhang.migration.core.reader.MigrationReader;
import priv.ethanzhang.migration.core.task.MigrationTask;
import priv.ethanzhang.migration.core.writer.MigrationWriter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static priv.ethanzhang.migration.core.context.MigrationState.*;

/**
 * 本地任务执行器
 */
@Getter
public class LocalMigrationTaskExecutor<I, O> extends AbstractMigrationTaskExecutor<I, O> {

    private final ListeningExecutorService executor;

    private ListenableFuture<?> readerTask;

    private ListenableFuture<?> processorTask;

    private ListenableFuture<?> writerTask;

    public LocalMigrationTaskExecutor(ExecutorService executor) {
        this.executor = MoreExecutors.listeningDecorator(executor);
    }

    @Override
    protected void startReader(MigrationTask<I, O> task) {
        readerTask =  executor.submit(() -> {
            MigrationContext<I, O> context = task.getContext();
            MigrationReader<I> reader = task.getReader();
            DataBuffer<I> readBuffer = context.getReadBuffer();
            MigrationConfigAttributes attributes = MigrationConfigAttributes.fromClass(reader.getClass());
            Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                    .retryIfResult(Boolean.FALSE::equals)
                    .withStopStrategy(StopStrategies.stopAfterAttempt(attributes.getMaxProduceRetryTimes()))
                    .withWaitStrategy(WaitStrategies.fixedWait(attributes.getProduceRetryPeriodSeconds(), TimeUnit.SECONDS))
                    .build();
            reader.initialize(context);
            while (context.getReaderState() == RUNNING) {
                if (Thread.currentThread().isInterrupted()) {
                    context.setReaderState(TERMINATED);
                    return;
                }
                MigrationChunk<I> chunk;
                try {
                    chunk = reader.read(context);
                } catch (Exception e) {
                    Set<Class<? extends Throwable>> interruptFor = attributes.getInterruptFor();
                    if (interruptFor.stream().anyMatch(t -> t.isAssignableFrom(e.getClass()))) {
                        context.setReaderState(FAILED);
                        reader.destroy(context);
                        task.getDispatcher().dispatch(new TaskTaskFailedEvent(task, TaskTaskFailedEvent.Cause.READER_FAILED, e));
                        return;
                    } else {
                        task.getDispatcher().dispatch(new TaskTaskWarnningEvent(task, TaskTaskWarnningEvent.Cause.READER_FAILED, e));
                        continue;
                    }
                }
                if (chunk.isEmpty()) {
                    context.setReaderState(TERMINATED);
                    reader.destroy(context);
                    return;
                } else {
                    for (I i : chunk) {
                        try {
                            if (retryer.call(() -> readBuffer.tryProduce(i))) {
                                context.incrReadCount(1);
                            }
                        } catch (ExecutionException | RetryException e) {
                            task.getDispatcher().dispatch(new TaskTaskWarnningEvent(task, TaskTaskWarnningEvent.Cause.READER_TO_BUFFER_FAILED, e));
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void startProcessor(MigrationTask<I, O> task) {
        processorTask = executor.submit(() -> {
            MigrationContext<I, O> context = task.getContext();
            MigrationProcessor<I, O> processor = task.getProcessor();
            DataBuffer<I> readBuffer = context.getReadBuffer();
            DataBuffer<O> writeBuffer = context.getWriteBuffer();
            MigrationConfigAttributes attributes = MigrationConfigAttributes.fromClass(processor.getClass());
            Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                    .retryIfResult(Boolean.FALSE::equals)
                    .withStopStrategy(StopStrategies.stopAfterAttempt(attributes.getMaxProduceRetryTimes()))
                    .withWaitStrategy(WaitStrategies.fixedWait(attributes.getProduceRetryPeriodSeconds(), TimeUnit.SECONDS))
                    .build();
            while (context.getReaderState() == RUNNING || !readBuffer.isEmpty()) {
                if (Thread.currentThread().isInterrupted()) {
                    context.setProcessorState(TERMINATED);
                    return;
                }
                MigrationChunk<O> output;
                try {
                    List<I> input = readBuffer.consumeIfPossible(attributes.getMaxConsumeCount());
                    if (CollectionUtils.isEmpty(input)) {
                        Thread.yield();
                        continue;
                    } else {
                        output = processor.process(context, MigrationChunk.of(input));
                    }
                } catch (Exception e) {
                    if (attributes.shouldInterruptFor(e)) {
                        context.setProcessorState(FAILED);
                        task.getDispatcher().dispatch(new TaskTaskFailedEvent(task, TaskTaskFailedEvent.Cause.PROCESSOR_FAILED, e));
                        return;
                    } else {
                        task.getDispatcher().dispatch(new TaskTaskWarnningEvent(task, TaskTaskWarnningEvent.Cause.PROCESSOR_FAILED, e));
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
                            task.getDispatcher().dispatch(new TaskTaskWarnningEvent(task, TaskTaskWarnningEvent.Cause.PROCESSOR_TO_BUFFER_FAILED, e));
                        }
                    }
                }
            }
            context.setProcessorState(context.getReaderState());
        });
    }

    @Override
    protected void startWriter(MigrationTask<I, O> task) {
        writerTask = executor.submit(() -> {
            MigrationContext<I, O> context = task.getContext();
            MigrationWriter<O> writer = task.getWriter();
            DataBuffer<O> writeBuffer = context.getWriteBuffer();
            MigrationConfigAttributes attributes = MigrationConfigAttributes.fromClass(writer.getClass());
            writer.initialize(context);
            while (context.getProcessorState() == RUNNING || !writeBuffer.isEmpty()) {
                if (Thread.currentThread().isInterrupted()) {
                    context.setWriterState(TERMINATED);
                    return;
                }
                try {
                    List<O> output = writeBuffer.consumeIfPossible(attributes.getMaxConsumeCount());
                    if (CollectionUtils.isEmpty(output)) {
                        Thread.yield();
                    } else {
                        context.incrWrittenCount(writer.write(context, MigrationChunk.of(output)));
                    }
                } catch (Exception e) {
                    if (attributes.shouldInterruptFor(e)) {
                        context.setWriterState(FAILED);
                        writer.destroy(context);
                        task.getDispatcher().dispatch(new TaskTaskFailedEvent(task, TaskTaskFailedEvent.Cause.WRITER_FAILED, e));
                        return;
                    } else {
                        task.getDispatcher().dispatch(new TaskTaskWarnningEvent(task, TaskTaskWarnningEvent.Cause.WRITER_FAILED, e));
                    }
                }
            }
            context.setWriterState(context.getProcessorState());
            if (context.getWriterState() == TERMINATED || context.getWriterState() == FAILED) {
                writer.destroy(context);
            }
            if (context.isTerminated()) {
                task.getDispatcher().dispatch(new TaskTaskFinishedEvent(task));
            }
        });
    }

    @Override
    protected void shutdownReader(MigrationTask<I, O> task) {
        Optional.ofNullable(readerTask).ifPresent(t -> t.cancel(true));
    }

    @Override
    protected void shutdownProcessor(MigrationTask<I, O> task) {
        Optional.ofNullable(processorTask).ifPresent(t -> t.cancel(true));
    }

    @Override
    protected void shutdownWriter(MigrationTask<I, O> task) {
        Optional.ofNullable(writerTask).ifPresent(t -> t.cancel(true));
    }

}
