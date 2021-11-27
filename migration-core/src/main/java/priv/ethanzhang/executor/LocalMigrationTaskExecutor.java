package priv.ethanzhang.executor;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.buffer.MigrationBuffer;
import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;
import priv.ethanzhang.context.MigrationState;
import priv.ethanzhang.processor.MigrationProcessor;
import priv.ethanzhang.reader.MigrationReader;
import priv.ethanzhang.task.MigrationTask;
import priv.ethanzhang.writer.MigrationWriter;

import java.util.List;
import java.util.concurrent.*;

/**
 * 本地任务执行器
 */
@Getter
public class LocalMigrationTaskExecutor implements MigrationTaskExecutor {

    private final ListeningExecutorService executor;

    private ListenableFutureTask<Void> readerTask;

    private ListenableFutureTask<Void> processorTask;

    private ListenableFutureTask<Void> writerTask;

    public LocalMigrationTaskExecutor(ExecutorService executor) {
        this.executor = MoreExecutors.listeningDecorator(executor);
    }

    @Override
    public <I, O> void execute(MigrationTask<I, O> task) {
        MigrationReader<I> reader = task.getReader();
        MigrationProcessor<I, O> processor = task.getProcessor();
        MigrationWriter<O> writer = task.getWriter();
        MigrationContext<I, O> context = task.getContext();
        MigrationBuffer<I> readBuffer = context.getReadBuffer();
        MigrationBuffer<O> writeBuffer = context.getWriteBuffer();
        readerTask = ListenableFutureTask.create(() -> {
            do {
                MigrationChunk<I> chunk = reader.read(context);
                if (chunk.isNotEmpty()) {
                    readBuffer.publishBatch(chunk.toList());
                } else {
                    break;
                }
            } while (context.getReaderState() == MigrationState.RUNNING);
        }, null);

        processorTask = ListenableFutureTask.create(() -> {
            do {
                List<I> chunk = readBuffer.subscribeAll();
                if (CollectionUtils.isNotEmpty(chunk)) {
                    MigrationChunk<O> processedChunk = processor.process(context, MigrationChunk.ofList(chunk));
                    if (processedChunk.isNotEmpty()) {
                        writeBuffer.publishBatch(processedChunk.toList());
                    }
                }
            } while (!readBuffer.isEmpty() || context.getReaderState() == MigrationState.RUNNING);
        }, null);

        writerTask = ListenableFutureTask.create(() -> {
            do {
                List<O> chunk = writeBuffer.subscribeAll();
                if (CollectionUtils.isNotEmpty(chunk)) {
                    writer.write(context, MigrationChunk.ofList(chunk));
                }
            } while (!writeBuffer.isEmpty() || context.getProcessorState() == MigrationState.RUNNING);
        }, null);
    }

}
