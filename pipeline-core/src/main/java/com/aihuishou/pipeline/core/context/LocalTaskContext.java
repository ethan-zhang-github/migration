package com.aihuishou.pipeline.core.context;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.common.*;
import com.aihuishou.pipeline.core.task.PipeTask;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;

/**
 * 本地任务上下文
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
@Builder
public class LocalTaskContext<I, O> implements TaskContext<I, O> {

    private final PipeTask<I, O> task;

    private final TaskParameter parameter;

    private final DataBuffer<I> readBuffer;

    private final DataBuffer<O> writeBuffer;

    private final Counter readerCounter = new LocalCounter();

    private final Counter processorCounter = new LocalCounter();

    private final Counter writerCounter = new LocalCounter();

    private final Holder<TaskState> readerState = new LocalTaskStateHolder();

    private final Holder<TaskState> processorState = new LocalTaskStateHolder();

    private final Holder<TaskState> writerState = new LocalTaskStateHolder();

    private final Holder<Long> total = new LocalHolder<>();

    private final Holder<Instant> startTimestamap = new LocalOnceHolder<>();

    private final Holder<Instant> finishTimestamap = new LocalOnceHolder<>();

    private final Holder<Duration> reportPeriod = new LocalHolder<>();

    private final Holder<Duration> timeout = new LocalHolder<>();

    @Override
    public TaskParameter getParameter() {
        return parameter;
    }

    @Override
    public PipeTask<I, O> getTask() {
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
    public Counter getReadCounter() {
        return readerCounter;
    }

    @Override
    public Counter getProcessedCounter() {
        return processorCounter;
    }

    @Override
    public Counter getWrittenCounter() {
        return writerCounter;
    }

    @Override
    public Holder<TaskState> getReaderState() {
        return readerState;
    }

    @Override
    public Holder<TaskState> getProcessorState() {
        return processorState;
    }

    @Override
    public Holder<TaskState> getWriterState() {
        return writerState;
    }

    @Override
    public Holder<Long> getTotal() {
        return total;
    }

    @Override
    public Holder<Instant> getStartTimestamp() {
        return startTimestamap;
    }

    @Override
    public Holder<Instant> getFinishTimestamp() {
        return finishTimestamap;
    }

    @Override
    public Duration getCost() {
        if (!startTimestamap.isPresent()) {
            return Duration.ZERO;
        }
        if (!finishTimestamap.isPresent()) {
            return Duration.between(startTimestamap.get(), Instant.now());
        }
        return Duration.between(startTimestamap.get(), finishTimestamap.get());
    }

    @Override
    public Holder<Duration> getReportPeriod() {
        return reportPeriod;
    }

    @Override
    public Holder<Duration> getTimeout() {
        return timeout;
    }

    @Override
    public boolean isTimeout() {
        if (!timeout.isPresent()) {
            return false;
        }
        if (!startTimestamap.isPresent()) {
            return false;
        }
        if (!finishTimestamap.isPresent()) {
            return false;
        }
        return Duration.between(startTimestamap.get(), Instant.now()).compareTo(timeout.get()) > 0;
    }

}
