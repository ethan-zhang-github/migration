package com.aihuishou.pipeline.core.writer;

import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;

/**
 * 数据写入
 * @param <O> 写入类型
 * @author ethan zhang
 */
@FunctionalInterface
public interface PipeWriter<O> {

    int write(TaskContext<?, O> context, DataChunk<O> output);

    default void initialize(TaskContext<?, O> context) {}

    default void destroy(TaskContext<?, O> context) {}

}
