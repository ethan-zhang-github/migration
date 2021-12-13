package priv.ethanzhang.pipeline.core.reader;

import priv.ethanzhang.pipeline.core.context.TaskContext;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class OnceInitializedReader<I> implements PipeReader<I> {

    private final AtomicBoolean initialized = new AtomicBoolean();

    private final AtomicBoolean destroyed = new AtomicBoolean();

    @Override
    public void initialize(TaskContext<I, ?> context) {
        if (initialized.compareAndSet(false, true)) {
            initializeInternal(context);
        }
    }

    @Override
    public void destroy(TaskContext<I, ?> context) {
        if (destroyed.compareAndSet(false, true)) {
            destroyInternal(context);
        }
    }

    protected void initializeInternal(TaskContext<I, ?> context) {}

    protected void destroyInternal(TaskContext<I, ?> context) {}

}
