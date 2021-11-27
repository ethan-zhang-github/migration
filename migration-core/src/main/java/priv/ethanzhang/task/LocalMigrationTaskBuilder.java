package priv.ethanzhang.task;

import priv.ethanzhang.buffer.LocalMigrationBuffer;
import priv.ethanzhang.context.LocalMigrationContext;
import priv.ethanzhang.executor.LocalMigrationTaskExecutor;
import priv.ethanzhang.manager.LocalMigrationTaskManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalMigrationTaskBuilder<I, O> extends AbstractMigrationTaskBuilder<I, O> {

    private int readBufferSize = Integer.MAX_VALUE;

    private int writeBufferSize = Integer.MAX_VALUE;

    private ExecutorService executor = Executors.newFixedThreadPool(3);

    public LocalMigrationTaskBuilder<I, O> readerBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> writeBufferSize(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    protected void customBuild(MigrationTask<I, O> task) {
        LocalMigrationContext<I, O> context = LocalMigrationContext.<I, O>builder()
                .task(task)
                .parameter(parameter)
                .readBuffer(new LocalMigrationBuffer<>(readBufferSize))
                .writeBuffer(new LocalMigrationBuffer<>(writeBufferSize))
                .build();
        task.setContext(context);
        task.setExecutor(new LocalMigrationTaskExecutor(executor));
        task.setManager(LocalMigrationTaskManager.INSTANCE);
    }

}
