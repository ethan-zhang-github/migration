package priv.ethanzhang.writer;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

public abstract class PartitionedMigrationWriter<O> implements MigrationWriter<O> {

    private final int size;

    public PartitionedMigrationWriter(int size) {
        this.size = size;
    }

    @Override
    public int write(MigrationContext<?, O> context, MigrationChunk<O> output) {
        return output.partition(size).stream().mapToInt(i -> writeInternal(context, i)).sum();
    }

    protected abstract int writeInternal(MigrationContext<?, O> context, MigrationChunk<O> output);

}
