package priv.ethanzhang.context;

import priv.ethanzhang.buffer.MigrationBuffer;
import priv.ethanzhang.task.MigrationTask;

public interface MigrationContext<I, O> {

    MigrationParameter getParameter();

    MigrationTask<I, O> getTask();

    MigrationBuffer<I> getReadBuffer();

    MigrationBuffer<O> getWriteBuffer();

    MigrationState getState();

    long getReadCount();

    long getProcessedCount();

    long getWrittenCount();

    boolean readerDown();

    boolean processorDown();

    boolean writerDown();

}
