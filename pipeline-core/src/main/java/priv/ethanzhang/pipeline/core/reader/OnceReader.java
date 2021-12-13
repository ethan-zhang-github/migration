package priv.ethanzhang.pipeline.core.reader;

import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 一次性读取
 * @param <I> 读取类型
 */
public abstract class OnceReader<I> implements PipeReader<I> {

    private final AtomicBoolean mark = new AtomicBoolean();

    @Override
    public DataChunk<I> read(TaskContext<I, ?> context) {
        if (mark.compareAndSet(false, true)) {
            return readOnce(context);
        }
        return DataChunk.empty();
    }

    protected abstract DataChunk<I> readOnce(TaskContext<I, ?> context);

}
