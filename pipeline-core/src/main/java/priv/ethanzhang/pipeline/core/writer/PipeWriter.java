package priv.ethanzhang.pipeline.core.writer;

import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskContext;

/**
 * 数据写入
 * @param <O> 写入类型
 */
@FunctionalInterface
public interface PipeWriter<O> {

    int write(TaskContext<?, O> context, DataChunk<O> output);

    default void initialize(TaskContext<?, O> context) {}

    default void destroy(TaskContext<?, O> context) {}

}
