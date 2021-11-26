package priv.ethanzhang.task;

import priv.ethanzhang.buffer.LocalMigrationBuffer;
import priv.ethanzhang.context.LocalMigrationContext;
import priv.ethanzhang.manager.LocalMigrationTaskManager;

public class LocalMigrationTaskBuilder<I, O> extends AbstractMigrationTaskBuilder<I, O> {

    private int readBufferSize;

    private int writeBufferSize;

    public LocalMigrationTaskBuilder<I, O> readerBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
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
        task.setManager(LocalMigrationTaskManager.INSTANCE);
    }

}
