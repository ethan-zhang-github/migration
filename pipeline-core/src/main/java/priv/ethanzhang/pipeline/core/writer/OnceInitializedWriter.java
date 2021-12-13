package priv.ethanzhang.pipeline.core.writer;

import priv.ethanzhang.pipeline.core.context.TaskContext;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class OnceInitializedWriter<O> implements PipeWriter<O> {

    private final AtomicBoolean initialized = new AtomicBoolean();

    private final AtomicBoolean destroyed = new AtomicBoolean();

    @Override
    public void initialize(TaskContext<?, O> context) {
        if (initialized.compareAndSet(false, true)) {
            initializeInternal(context);
        }
    }

    @Override
    public void destroy(TaskContext<?, O> context) {
        if (destroyed.compareAndSet(false, true)) {
            destroyInternal(context);
        }
    }

    protected void initializeInternal(TaskContext<?, O> context) {}

    protected void destroyInternal(TaskContext<?, O> context) {}

}
