package priv.ethanzhang.migration.core.context;

import lombok.Builder;
import priv.ethanzhang.migration.core.buffer.DataBuffer;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
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

    private final DataBuffer<I> readBuffer;

    private final DataBuffer<O> writeBuffer;

    private final LongAdder readerCounter = new LongAdder();

    private final LongAdder processorCounter = new LongAdder();

    private final LongAdder writerCounter = new LongAdder();

    private final MigrationStateHolder readerState = new MigrationStateHolder();

    private final MigrationStateHolder processorState = new MigrationStateHolder();

    private final MigrationStateHolder writerState = new MigrationStateHolder();

    private final AtomicLong totalCounter = new AtomicLong();

    private final AtomicReference<Instant> startTimestamap = new AtomicReference<>();

    private final AtomicReference<Instant> finishTimestamap = new AtomicReference<>();

    private Duration reportPeriod;

    @Override
    public MigrationParameter getParameter() {
        return parameter;
    }

    @Override
    public MigrationTask<I, O> getTask() {
        return task;
    }

    @Override
    public DataBuffer<I> getReadBuffer() {
        return readBuffer;
    }

    @Override
    public DataBuffer<O> getWriteBuffer() {
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

    @Override
    public long getTotal() {
        return totalCounter.get();
    }

    @Override
    public void setTotal(long total) {
        totalCounter.set(total);
    }

    @Override
    public Instant getStartTimestamp() {
        return startTimestamap.get();
    }

    @Override
    public void setStartTimestamp(Instant instant) {
        startTimestamap.compareAndSet(null, instant);
    }

    @Override
    public Instant getFinishTimestamp() {
        return finishTimestamap.get();
    }

    @Override
    public void setFinishTimestamp(Instant instant) {
        finishTimestamap.compareAndSet(null, instant);
    }

    @Override
    public Duration getCost() {
        if (startTimestamap.get() == null) {
            return Duration.ZERO;
        }
        if (finishTimestamap.get() == null) {
            return Duration.between(startTimestamap.get(), Instant.now());
        }
        return Duration.between(startTimestamap.get(), finishTimestamap.get());
    }

    @Override
    public void setReportPeriod(Duration reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    @Override
    public Duration getReportPeriod() {
        return reportPeriod;
    }

}
