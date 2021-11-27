package priv.ethanzhang.context;

import priv.ethanzhang.buffer.MigrationBuffer;
import priv.ethanzhang.task.MigrationTask;

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

    long getProcessedCount();

    long getWrittenCount();

    MigrationState getReaderState();

    MigrationState getProcessorState();

    MigrationState getWriterState();

    void setReaderState(MigrationState state);

    void setProcessorState(MigrationState state);

    void setWriterState(MigrationState state);

}
