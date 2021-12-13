package priv.ethanzhang.pipeline.core.reader;

import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskContext;

/**
 * 数据读取
 * @param <I> 读取类型
 */
@FunctionalInterface
public interface PipeReader<I> {

    DataChunk<I> read(TaskContext<I, ?> context);

    default void initialize(TaskContext<I, ?> context) {}

    default void destroy(TaskContext<I, ?> context) {}

}
