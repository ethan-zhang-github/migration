package priv.ethanzhang.pipeline.core.writer;

import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskContext;
import priv.ethanzhang.pipeline.core.utils.BatchUtil;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class PartitionedConcurrentWriter<O> implements PipeWriter<O> {

    private final int size;

    private final Executor executor;

    public PartitionedConcurrentWriter(int size, Executor executor) {
        this.size = size;
        this.executor = executor;
    }

    @Override
    public int write(TaskContext<?, O> context, DataChunk<O> output) {
        AtomicInteger counter = new AtomicInteger();
        BatchUtil.partitionAndProcess(output.toList(), size, (List<O> list) -> counter.addAndGet(writeInternal(context, DataChunk.of(list))), executor);
        return counter.get();
    }

    protected abstract int writeInternal(TaskContext<?, O> context, DataChunk<O> output);

}
