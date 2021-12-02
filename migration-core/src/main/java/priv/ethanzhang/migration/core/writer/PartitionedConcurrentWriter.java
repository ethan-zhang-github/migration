package priv.ethanzhang.migration.core.writer;

import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.utils.BatchUtil;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PartitionedConcurrentWriter<O> implements MigrationWriter<O> {

    private final int size;

    private final Executor executor;

    public PartitionedConcurrentWriter(int size, Executor executor) {
        this.size = size;
        this.executor = executor;
    }

    @Override
    public int write(MigrationContext<?, O> context, MigrationChunk<O> output) {
        AtomicInteger counter = new AtomicInteger();
        BatchUtil.partitionAndProcess(output.toList(), size, (List<O> list) -> counter.addAndGet(writeInternal(context, MigrationChunk.of(list))), executor);
        return counter.get();
    }

    protected abstract int writeInternal(MigrationContext<?, O> context, MigrationChunk<O> output);

}
