package priv.ethanzhang.migration.core.writer;

import priv.ethanzhang.migration.core.context.MigrationContext;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class OnceInitializedWriter<O> implements MigrationWriter<O> {

    private final AtomicBoolean initialized = new AtomicBoolean();

    private final AtomicBoolean destroyed = new AtomicBoolean();

    @Override
    public void initialize(MigrationContext<?, O> context) {
        if (initialized.compareAndSet(false, true)) {
            initializeInternal(context);
        }
    }

    @Override
    public void destroy(MigrationContext<?, O> context) {
        if (destroyed.compareAndSet(false, true)) {
            destroyInternal(context);
        }
    }

    protected void initializeInternal(MigrationContext<?, O> context) {}

    protected void destroyInternal(MigrationContext<?, O> context) {}

}
