package com.aihuishou.pipeline.core.context;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.common.Counter;
import com.aihuishou.pipeline.core.common.Holder;
import com.aihuishou.pipeline.core.task.PipeTask;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@Setter
public abstract class AbstractTaskContext<I, O> implements TaskContext<I, O> {

    protected PipeTask<I, O> task;

    protected TaskParameter parameter;

    protected DataBuffer<I> readBuffer;

    protected DataBuffer<O> writeBuffer;

    protected Counter readerCounter;

    protected Counter processorCounter;

    protected Counter writerCounter;

    protected Holder<TaskState> readerState;

    protected Holder<TaskState> processorState;

    protected Holder<TaskState> writerState;

    protected Holder<Long> total;

    protected Holder<Instant> startTime;

    protected Holder<Instant> finishTime;

    protected Holder<Duration> reportPeriod;

    protected Holder<Duration> timeout;

    protected AbstractTaskContext() {}

    @Override
    public PipeTask<I, O> getTask() {
        return task;
    }

    @Override
    public TaskParameter getParameter() {
        return parameter;
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
    public Counter getReaderCounter() {
        return readerCounter;
    }

    @Override
    public Counter getProcessorCounter() {
        return processorCounter;
    }

    @Override
    public Counter getWriterCounter() {
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
    public Holder<Instant> getStartTime() {
        return startTime;
    }

    @Override
    public Holder<Instant> getFinishTime() {
        return finishTime;
    }

    @Override
    public Holder<Duration> getReportPeriod() {
        return reportPeriod;
    }

    @Override
    public Holder<Duration> getTimeout() {
        return timeout;
    }

}
