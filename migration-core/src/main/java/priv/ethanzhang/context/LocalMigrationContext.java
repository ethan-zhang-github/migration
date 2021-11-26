package priv.ethanzhang.context;

import lombok.Builder;
import priv.ethanzhang.buffer.MigrationBuffer;
import priv.ethanzhang.task.MigrationTask;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

@Builder
public class LocalMigrationContext<I, O> implements MigrationContext<I, O> {

    private final MigrationTask<I, O> task;

    private final MigrationParameter parameter;

    private final MigrationBuffer<I> readBuffer;

    private final MigrationBuffer<O> writeBuffer;

    private final AtomicReference<MigrationState> state = new AtomicReference<>(MigrationState.NEW);

    private final LongAdder readerCounter = new LongAdder();

    private final LongAdder processorCounter = new LongAdder();

    private final LongAdder writerCounter = new LongAdder();

    private final AtomicBoolean readerDownMark = new AtomicBoolean();

    private final AtomicBoolean processorDownMark = new AtomicBoolean();

    private final AtomicBoolean writerDownMark = new AtomicBoolean();

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
    public MigrationState getState() {
        return state.get();
    }

    @Override
    public long getReadCount() {
        return readerCounter.longValue();
    }

    @Override
    public long getProcessedCount() {
        return processorCounter.longValue();
    }

    @Override
    public long getWrittenCount() {
        return writerCounter.longValue();
    }

    @Override
    public boolean readerDown() {
        return readerDownMark.get();
    }

    @Override
    public boolean processorDown() {
        return processorDownMark.get();
    }

    @Override
    public boolean writerDown() {
        return writerDownMark.get();
    }

}
