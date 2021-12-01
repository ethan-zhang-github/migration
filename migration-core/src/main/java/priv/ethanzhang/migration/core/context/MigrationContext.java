package priv.ethanzhang.migration.core.context;

import priv.ethanzhang.migration.core.buffer.MigrationBuffer;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.time.Duration;
import java.time.Instant;

/**
 * 任务上下文
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
public interface MigrationContext<I, O> {

    MigrationParameter getParameter();

    MigrationTask<I, O> getTask();

    MigrationBuffer<I> getReadBuffer();

    MigrationBuffer<O> getWriteBuffer();

    long getReadCount();

    void incrReadCount(long count);

    long getProcessedCount();

    void incrProcessedCount(long count);

    long getWrittenCount();

    void incrWrittenCount(long count);

    MigrationState getReaderState();

    MigrationState getProcessorState();

    MigrationState getWriterState();

    void setReaderState(MigrationState state);

    void setProcessorState(MigrationState state);

    void setWriterState(MigrationState state);

    long getTotal();

    void setTotal(long total);

    Instant getStartTimestamp();

    void setStartTimestamp(Instant instant);

    Instant getFinishTimestamp();

    void setFinishTimestamp(Instant instant);

    Duration getCost();

    void setReportPeriod(Duration reportPeriod);

    Duration getReportPeriod();

    default boolean isTerminated() {
        return getReaderState() == MigrationState.TERMINATED && getProcessorState() == MigrationState.TERMINATED && getWriterState() == MigrationState.TERMINATED;
    }

    default boolean isFailed() {
        return getReaderState() == MigrationState.FAILED || getProcessorState() == MigrationState.FAILED || getWriterState() == MigrationState.FAILED;
    }

}
