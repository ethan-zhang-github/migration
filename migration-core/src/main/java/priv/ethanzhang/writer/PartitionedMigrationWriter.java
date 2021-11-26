package priv.ethanzhang.writer;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

public abstract class PartitionedMigrationWriter<O> implements MigrationWriter<O> {

    private final int size;

    public PartitionedMigrationWriter(int size) {
        this.size = size;
    }

    @Override
    public void write(MigrationContext<?, O> context, MigrationChunk<O> output) {
        output.partition(size).forEach(i -> writeInternal(context, i));
    }

    protected abstract void writeInternal(MigrationContext<?, O> context, MigrationChunk<O> output);

}
