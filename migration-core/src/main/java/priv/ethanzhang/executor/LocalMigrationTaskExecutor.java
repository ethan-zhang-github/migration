package priv.ethanzhang.executor;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Reflection;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.annotation.MigrationConfig;
import priv.ethanzhang.annotation.MigrationConfigAttributes;
import priv.ethanzhang.buffer.MigrationBuffer;
import priv.ethanzhang.config.GlobalConfig;
import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;
import priv.ethanzhang.context.MigrationState;
import priv.ethanzhang.event.MigrationTaskFailedEvent;
import priv.ethanzhang.event.MigrationTaskWarnningEvent;
import priv.ethanzhang.manager.LocalMigrationTaskManager;
import priv.ethanzhang.processor.MigrationProcessor;
import priv.ethanzhang.reader.MigrationReader;
import priv.ethanzhang.task.MigrationTask;
import priv.ethanzhang.writer.MigrationWriter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static priv.ethanzhang.context.MigrationState.*;

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
    protected void executeReader(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        MigrationReader<I> reader = task.getReader();
        MigrationBuffer<I> readBuffer = context.getReadBuffer();
        readerTask =  executor.submit(() -> {
            context.setReaderState(RUNNING);

            MigrationConfigAttributes attributes = MigrationConfigAttributes.fromClass(reader.getClass());
            Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder().retryIfResult(Boolean.TRUE::equals)
                    .withStopStrategy(StopStrategies.stopAfterAttempt(attributes.getMaxProduceRetryTimes())).build();

            try {
                do {
                    if (Thread.currentThread().isInterrupted()) {
                        context.setReaderState(STOPPING);
                        return;
                    }
                    MigrationChunk<I> chunk = reader.read(context);
                    if (chunk.isNotEmpty()) {
                        for (I i : chunk) {
                            boolean success = retryer.call(() -> readBuffer.tryProduce(i, GlobalConfig.READER.getProduceWaitSeconds(), TimeUnit.SECONDS));
                            if (success) {
                                context.incrReadCount(1);
                            } else {
                                LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskWarnningEvent(task, MigrationTaskWarnningEvent.Cause.READER_PRODUCE_FAILED));
                            }
                        }
                    } else {
                        context.setReaderState(TERMINATED);
                        break;
                    }
                } while (context.getReaderState() == RUNNING);
            } catch (Exception e) {
                context.setReaderState(MigrationState.FAILED);
                LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskFailedEvent(task, MigrationTaskFailedEvent.Cause.READER_FAILED, e));
            }
        });
    }

    @Override
    protected void executeProcessor(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        MigrationProcessor<I, O> processor = task.getProcessor();
        MigrationBuffer<I> readBuffer = context.getReadBuffer();
        MigrationBuffer<O> writeBuffer = context.getWriteBuffer();
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(Boolean.TRUE::equals)
                .withStopStrategy(StopStrategies.stopAfterAttempt(GlobalConfig.PROCESSOR.getProduceRetryTimes()))
                .build();
        processorTask = executor.submit(() -> {
            context.setProcessorState(RUNNING);
            try {
                do {
                    List<I> input = readBuffer.consumeIfPossible(readBuffer.size());
                    if (CollectionUtils.isNotEmpty(input)) {
                        MigrationChunk<O> output = processor.process(context, MigrationChunk.ofList(input));
                        if (output.isNotEmpty()) {
                            for (O o : output) {
                                boolean success = retryer.call(() -> writeBuffer.tryProduce(o, GlobalConfig.PROCESSOR.getProduceWaitSeconds(), TimeUnit.SECONDS));
                                if (success) {
                                    context.incrProcessedCount(1);
                                } else {
                                    LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskWarnningEvent(task, MigrationTaskWarnningEvent.Cause.PROCESSOR_PRODUCE_FAILED));
                                }
                            }
                        }
                    } else {
                        Thread.yield();
                    }
                } while (context.getReaderState() == RUNNING || !readBuffer.isEmpty());

                if (context.getReaderState() == STOPPING) {
                    context.setProcessorState(STOPPING);
                }
            } catch (Exception e) {
                context.setProcessorState(MigrationState.FAILED);
                LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskFailedEvent(task, MigrationTaskFailedEvent.Cause.PROCESSOR_FAILED, e));
            }
        });
    }

    @Override
    protected void executeWriter(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        MigrationWriter<O> writer = task.getWriter();
        MigrationBuffer<O> writeBuffer = context.getWriteBuffer();
        writerTask = executor.submit(() -> {
            context.setWriterState(RUNNING);
            try {
                do {
                    List<O> output = writeBuffer.consumeIfPossible(writeBuffer.size());
                    if (CollectionUtils.isNotEmpty(output)) {
                        int count = writer.write(context, MigrationChunk.ofList(output));
                        context.incrWrittenCount(count);
                    } else {
                        Thread.yield();
                    }
                } while (context.getProcessorState() == RUNNING || !writeBuffer.isEmpty());
                if (context.getProcessorState() == STOPPING) {
                    context.setWriterState(STOPPING);
                } else {
                    context.setWriterState(TERMINATED);
                }
            } catch (Exception e) {
                context.setWriterState(FAILED);
                LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskFailedEvent(task, MigrationTaskFailedEvent.Cause.WRITER_FAILED, e));
            }
        });
    }

    @Override
    protected void stopReader(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (readerTask != null && context.getReaderState() == RUNNING) {
            context.setReaderState(STOPPING);
        }
    }

    @Override
    protected void stopProcessor(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (processorTask != null && context.getProcessorState() == RUNNING) {
            context.setProcessorState(STOPPING);
        }
    }

    @Override
    protected void stopWriter(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (writerTask != null && context.getWriterState() == RUNNING) {
            context.setWriterState(STOPPING);
        }
    }

    @Override
    protected void shutdownReader(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (readerTask != null) {
            context.setReaderState(TERMINATED);
            readerTask.cancel(true);
        }
    }

    @Override
    protected void shutdownProcessor(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (processorTask != null) {
            context.setProcessorState(TERMINATED);
            processorTask.cancel(true);
        }
    }

    @Override
    protected void shutdownWriter(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (writerTask != null) {
            context.setWriterState(TERMINATED);
            writerTask.cancel(true);
        }
    }

}
