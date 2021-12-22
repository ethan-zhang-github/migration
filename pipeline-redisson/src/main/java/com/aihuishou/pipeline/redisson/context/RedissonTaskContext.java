package com.aihuishou.pipeline.redisson.context;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.common.Counter;
import com.aihuishou.pipeline.core.common.Holder;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.context.TaskParameter;
import com.aihuishou.pipeline.core.context.TaskState;
import com.aihuishou.pipeline.core.task.PipeTask;

import java.time.Duration;
import java.time.Instant;

public class RedissonTaskContext<I, O> implements TaskContext<I, O> {

    @Override
    public TaskParameter getParameter() {
        return null;
    }

    @Override
    public PipeTask<I, O> getTask() {
        return null;
    }

    @Override
    public DataBuffer<I> getReadBuffer() {
        return null;
    }

    @Override
    public DataBuffer<O> getWriteBuffer() {
        return null;
    }

    @Override
    public Counter getReadCounter() {
        return null;
    }

    @Override
    public Counter getProcessedCounter() {
        return null;
    }

    @Override
    public Counter getWrittenCounter() {
        return null;
    }

    @Override
    public Holder<TaskState> getReaderState() {
        return null;
    }

    @Override
    public Holder<TaskState> getProcessorState() {
        return null;
    }

    @Override
    public Holder<TaskState> getWriterState() {
        return null;
    }

    @Override
    public Holder<Long> getTotal() {
        return null;
    }

    @Override
    public Holder<Instant> getStartTimestamp() {
        return null;
    }

    @Override
    public Holder<Instant> getFinishTimestamp() {
        return null;
    }

    @Override
    public Duration getCost() {
        return null;
    }

    @Override
    public Holder<Duration> getReportPeriod() {
        return null;
    }

    @Override
    public Holder<Duration> getTimeout() {
        return null;
    }

    @Override
    public boolean isTimeout() {
        return false;
    }

}
