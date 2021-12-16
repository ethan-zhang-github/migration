package com.aihuishou.pipeline.core.reader;

import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;

/**
 * 迭代读取
 * @param <I> 读取类型
 */
public abstract class IterableReader<I> implements PipeReader<I> {

    @Override
    public DataChunk<I> read(TaskContext<I, ?> context) {
        if (!hasMore(context)) {
            return DataChunk.empty();
        }
        return readMore(context);
    }

    protected abstract boolean hasMore(TaskContext<I, ?> context);

    protected abstract DataChunk<I> readMore(TaskContext<I, ?> context);

}
