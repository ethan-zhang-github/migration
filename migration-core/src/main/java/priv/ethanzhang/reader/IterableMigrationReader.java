package priv.ethanzhang.reader;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

public abstract class IterableMigrationReader<I> implements MigrationReader<I> {

    @Override
    public MigrationChunk<I> read(MigrationContext<I, ?> context) {
        if (!hasMore(context)) {
            return MigrationChunk.empty();
        }
        return readMore(context);
    }

    protected abstract boolean hasMore(MigrationContext<I, ?> context);

    protected abstract MigrationChunk<I> readMore(MigrationContext<I, ?> context);

}
