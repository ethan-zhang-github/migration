package priv.ethanzhang.migration.core.context;

import priv.ethanzhang.migration.core.buffer.MigrationBuffer;
import priv.ethanzhang.migration.core.task.MigrationTask;

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

}
