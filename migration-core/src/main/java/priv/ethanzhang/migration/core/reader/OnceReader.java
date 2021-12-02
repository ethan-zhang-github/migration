package priv.ethanzhang.migration.core.reader;

import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 一次性读取
 * @param <I> 读取类型
 */
public abstract class OnceReader<I> implements MigrationReader<I> {

    private final AtomicBoolean mark = new AtomicBoolean();

    @Override
    public MigrationChunk<I> read(MigrationContext<I, ?> context) {
        if (mark.compareAndSet(false, true)) {
            return readOnce(context);
        }
        return MigrationChunk.empty();
    }

    protected abstract MigrationChunk<I> readOnce(MigrationContext<I, ?> context);

}
