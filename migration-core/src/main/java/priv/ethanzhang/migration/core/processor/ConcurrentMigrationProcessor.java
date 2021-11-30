package priv.ethanzhang.migration.core.processor;

import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.utils.BatchUtil;

import java.util.List;
import java.util.concurrent.Executor;

public abstract class ConcurrentMigrationProcessor<I, O> implements MigrationProcessor<I, O> {

    private final int size;

    private final Executor executor;

    public ConcurrentMigrationProcessor(int size, Executor executor) {
        this.size = size;
        this.executor = executor;
    }

    @Override
    public MigrationChunk<O> process(MigrationContext<I, ?> context, MigrationChunk<I> input) {
        return MigrationChunk.ofList(BatchUtil.partitionAndProcess(input.toList(), size,
                (List<I> list) -> processInternal(context, MigrationChunk.ofList(list)).toList(), executor));
    }

    protected abstract MigrationChunk<O> processInternal(MigrationContext<I, ?> context, MigrationChunk<I> input);

}
