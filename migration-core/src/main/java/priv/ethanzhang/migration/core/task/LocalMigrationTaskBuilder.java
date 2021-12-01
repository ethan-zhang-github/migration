package priv.ethanzhang.migration.core.task;

import priv.ethanzhang.migration.core.buffer.LocalMigrationBuffer;
import priv.ethanzhang.migration.core.context.LocalMigrationContext;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.event.LocalMigrationEventDispatcher;
import priv.ethanzhang.migration.core.executor.LocalMigrationTaskExecutor;
import priv.ethanzhang.migration.core.manager.LocalMigrationTaskManager;

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
        task.setExecutor(new LocalMigrationTaskExecutor<>(executor));
        task.setManager(LocalMigrationTaskManager.INSTANCE);
        task.setDispatcher(LocalMigrationEventDispatcher.INSTANCE);
    }

    @Override
    protected MigrationContext<I, O> buildContext(MigrationTask<I, O> task) {
        return LocalMigrationContext.<I, O>builder()
                .task(task)
                .parameter(parameter)
                .readBuffer(new LocalMigrationBuffer<>(readBufferSize))
                .writeBuffer(new LocalMigrationBuffer<>(writeBufferSize))
                .build();
    }

}
