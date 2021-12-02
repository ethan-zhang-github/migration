package priv.ethanzhang.migration.core.reader;

import priv.ethanzhang.migration.core.context.MigrationContext;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class OnceInitializedMigrationReader<I> implements MigrationReader<I> {

    private final AtomicBoolean initialized = new AtomicBoolean();

    private final AtomicBoolean destroyed = new AtomicBoolean();

    @Override
    public void initialize(MigrationContext<I, ?> context) {
        if (initialized.compareAndSet(false, true)) {
            initializeInternal(context);
        }
    }

    @Override
    public void destroy(MigrationContext<I, ?> context) {
        if (destroyed.compareAndSet(false, true)) {
            destroyInternal(context);
        }
    }

    protected void initializeInternal(MigrationContext<I, ?> context) {}

    protected void destroyInternal(MigrationContext<I, ?> context) {}

}
