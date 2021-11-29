package priv.ethanzhang.reader;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 一次性读取
 * @param <I> 读取类型
 */
public abstract class OnceMigrationReader<I> implements MigrationReader<I> {

    private final AtomicBoolean mark = new AtomicBoolean(false);

    @Override
    public MigrationChunk<I> read(MigrationContext<I, ?> context) {
        if (mark.get()) {
            return MigrationChunk.empty();
        }
        MigrationChunk<I> chunk = readOnce(context);
        mark.set(true);
        return chunk;
    }

    protected abstract MigrationChunk<I> readOnce(MigrationContext<I, ?> context);

}
