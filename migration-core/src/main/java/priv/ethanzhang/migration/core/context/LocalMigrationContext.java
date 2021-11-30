package priv.ethanzhang.migration.core.context;

import lombok.Builder;
import priv.ethanzhang.migration.core.buffer.MigrationBuffer;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.util.concurrent.atomic.LongAdder;

/**
 * 本地任务上下文
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
@Builder
public class LocalMigrationContext<I, O> implements MigrationContext<I, O> {

    private final MigrationTask<I, O> task;

    private final MigrationParameter parameter;

    private final MigrationBuffer<I> readBuffer;

    private final MigrationBuffer<O> writeBuffer;

    private final LongAdder readerCounter = new LongAdder();

    private final LongAdder processorCounter = new LongAdder();

    private final LongAdder writerCounter = new LongAdder();

    private final MigrationStateHolder readerState = new MigrationStateHolder();

    private final MigrationStateHolder processorState = new MigrationStateHolder();

    private final MigrationStateHolder writerState = new MigrationStateHolder();

    @Override
    public MigrationParameter getParameter() {
        return parameter;
    }

    @Override
    public MigrationTask<I, O> getTask() {
        return task;
    }

    @Override
    public MigrationBuffer<I> getReadBuffer() {
        return readBuffer;
    }

    @Override
    public MigrationBuffer<O> getWriteBuffer() {
        return writeBuffer;
    }

    @Override
    public long getReadCount() {
        return readerCounter.longValue();
    }

    @Override
    public void incrReadCount(long count) {
        readerCounter.add(count);
    }

    @Override
    public long getProcessedCount() {
        return processorCounter.longValue();
    }

    @Override
    public void incrProcessedCount(long count) {
        processorCounter.add(count);
    }

    @Override
    public long getWrittenCount() {
        return writerCounter.longValue();
    }

    @Override
    public void incrWrittenCount(long count) {
        writerCounter.add(count);
    }

    @Override
    public MigrationState getReaderState() {
        return readerState.get();
    }

    @Override
    public MigrationState getProcessorState() {
        return processorState.get();
    }

    @Override
    public MigrationState getWriterState() {
        return writerState.get();
    }

    @Override
    public void setReaderState(MigrationState state) {
        readerState.transfer(state);
    }

    @Override
    public void setProcessorState(MigrationState state) {
        processorState.transfer(state);
    }

    @Override
    public void setWriterState(MigrationState state) {
        writerState.transfer(state);
    }

}
