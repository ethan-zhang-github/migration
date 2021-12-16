package com.aihuishou.pipeline.core.processor;

import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;

/**
 * 数据中间处理
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
@FunctionalInterface
public interface PipeProcessor<I, O> {

    DataChunk<O> process(TaskContext<I, O> context, DataChunk<I> input);

}
