package priv.ethanzhang.migration.core.writer;

import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;

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
