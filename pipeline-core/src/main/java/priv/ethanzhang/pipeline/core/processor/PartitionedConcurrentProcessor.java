package priv.ethanzhang.pipeline.core.processor;

import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskContext;
import priv.ethanzhang.pipeline.core.utils.BatchUtil;

import java.util.List;
import java.util.concurrent.Executor;

public abstract class PartitionedConcurrentProcessor<I, O> implements PipeProcessor<I, O> {

    private final int size;

    private final Executor executor;

    public PartitionedConcurrentProcessor(int size, Executor executor) {
        this.size = size;
        this.executor = executor;
    }

    @Override
    public DataChunk<O> process(TaskContext<I, ?> context, DataChunk<I> input) {
        return DataChunk.of(BatchUtil.partitionAndProcess(input.toList(), size,
                (List<I> list) -> processInternal(context, DataChunk.of(list)).toList(), executor));
    }

    protected abstract DataChunk<O> processInternal(TaskContext<I, ?> context, DataChunk<I> input);

}
