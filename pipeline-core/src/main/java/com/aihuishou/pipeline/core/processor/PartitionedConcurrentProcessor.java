package com.aihuishou.pipeline.core.processor;

import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.utils.BatchUtil;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * 数据分片并发处理
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
public abstract class PartitionedConcurrentProcessor<I, O> implements PipeProcessor<I, O> {

    private final int size;

    private final Executor executor;

    public PartitionedConcurrentProcessor(int size) {
        this.size = size;
        this.executor = ForkJoinPool.commonPool();
    }

    public PartitionedConcurrentProcessor(int size, Executor executor) {
        this.size = size;
        this.executor = executor;
    }

    @Override
    public DataChunk<O> process(TaskContext<I, O> context, DataChunk<I> input) {
        return DataChunk.of(BatchUtil.partitionAndProcess(input.toList(), size,
                (List<I> list) -> processInternal(context, DataChunk.of(list)).toList(), executor));
    }

    protected abstract DataChunk<O> processInternal(TaskContext<I, O> context, DataChunk<I> input);

}
