package priv.ethanzhang.executor;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.buffer.MigrationBuffer;
import priv.ethanzhang.config.GlobalConfig;
import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;
import priv.ethanzhang.context.MigrationState;
import priv.ethanzhang.event.MigrationTaskFailedEvent;
import priv.ethanzhang.event.MigrationTaskWarnEvent;
import priv.ethanzhang.manager.LocalMigrationTaskManager;
import priv.ethanzhang.processor.MigrationProcessor;
import priv.ethanzhang.reader.MigrationReader;
import priv.ethanzhang.task.MigrationTask;
import priv.ethanzhang.writer.MigrationWriter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 本地任务执行器
 */
@Getter
public class LocalMigrationTaskExecutor<I, O> implements MigrationTaskExecutor<I, O> {

    private final ListeningExecutorService executor;

    private ListenableFuture<?> readerTask;

    private ListenableFuture<?> processorTask;

    private ListenableFuture<?> writerTask;

    public LocalMigrationTaskExecutor(ExecutorService executor) {
        this.executor = MoreExecutors.listeningDecorator(executor);
    }

    @Override
    public void execute(MigrationTask<I, O> task) {
        readerTask = executeReader(task);
        processorTask = executeProcessor(task);
        writerTask = executeWriter(task);
    }

    @Override
    public void stop(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (readerTask != null && context.getReaderState() == MigrationState.RUNNING) {
            context.setReaderState(MigrationState.STOPPING);
        }
        if (processorTask != null && context.getProcessorState() == MigrationState.RUNNING) {
            context.setProcessorState(MigrationState.STOPPING);
        }
    }

    @Override
    public void shutDown(MigrationTask<I, O> task) {

    }

    private ListenableFuture<?> executeReader(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        MigrationReader<I> reader = task.getReader();
        MigrationBuffer<I> readBuffer = context.getReadBuffer();
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(Boolean.TRUE::equals)
                .withStopStrategy(StopStrategies.stopAfterAttempt(GlobalConfig.READER.getProduceRetryTimes()))
                .build();
        return executor.submit(() -> {
            context.setReaderState(MigrationState.RUNNING);
            try {
                do {
                    if (Thread.currentThread().isInterrupted()) {
                        context.setReaderState(MigrationState.STOPPING);
                        return;
                    }
                    MigrationChunk<I> chunk = reader.read(context);
                    if (chunk.isNotEmpty()) {
                        for (I i : chunk) {
                            boolean success = retryer.call(() -> readBuffer.tryProduce(i, GlobalConfig.READER.getProduceWaitSeconds(), TimeUnit.SECONDS));
                            if (success) {
                                context.incrReadCount(1);
                            } else {
                                LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskWarnEvent(task, MigrationTaskWarnEvent.Cause.READER_PRODUCE_FAILED));
                            }
                        }
                    } else {
                        context.setReaderState(MigrationState.TERMINATED);
                        break;
                    }
                } while (context.getReaderState() == MigrationState.RUNNING);
            } catch (Exception e) {
                context.setReaderState(MigrationState.FAILED);
                LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskFailedEvent(task, MigrationTaskFailedEvent.Cause.READER_FAILED, e));
            }
        });
    }

    private ListenableFuture<?> executeProcessor(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        MigrationProcessor<I, O> processor = task.getProcessor();
        MigrationBuffer<I> readBuffer = context.getReadBuffer();
        MigrationBuffer<O> writeBuffer = context.getWriteBuffer();
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(Boolean.TRUE::equals)
                .withStopStrategy(StopStrategies.stopAfterAttempt(GlobalConfig.PROCESSOR.getProduceRetryTimes()))
                .build();
        return executor.submit(() -> {
            context.setProcessorState(MigrationState.RUNNING);
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
                                    LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskWarnEvent(task, MigrationTaskWarnEvent.Cause.PROCESSOR_PRODUCE_FAILED));
                                }
                            }
                        }
                    } else {
                        Thread.yield();
                    }
                } while (context.getReaderState() == MigrationState.RUNNING || !readBuffer.isEmpty());
                context.setProcessorState(MigrationState.TERMINATED);
            } catch (Exception e) {
                context.setProcessorState(MigrationState.FAILED);
                LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskFailedEvent(task, MigrationTaskFailedEvent.Cause.PROCESSOR_FAILED, e));
            }
        });
    }

    private ListenableFuture<?> executeWriter(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        MigrationWriter<O> writer = task.getWriter();
        MigrationBuffer<O> writeBuffer = context.getWriteBuffer();
        return executor.submit(() -> {
            context.setWriterState(MigrationState.RUNNING);
            try {
                do {
                    List<O> output = writeBuffer.consumeIfPossible(writeBuffer.size());
                    if (CollectionUtils.isNotEmpty(output)) {
                        int count = writer.write(context, MigrationChunk.ofList(output));
                        context.incrWrittenCount(count);
                    } else {
                        Thread.yield();
                    }
                } while (context.getProcessorState() == MigrationState.RUNNING || !writeBuffer.isEmpty());
                context.setWriterState(MigrationState.TERMINATED);
            } catch (Exception e) {
                context.setWriterState(MigrationState.FAILED);
                LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskFailedEvent(task, MigrationTaskFailedEvent.Cause.WRITER_FAILED, e));
            }
        });
    }

}
