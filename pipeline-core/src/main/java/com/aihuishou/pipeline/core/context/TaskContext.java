package com.aihuishou.pipeline.core.context;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.common.Counter;
import com.aihuishou.pipeline.core.common.Holder;
import com.aihuishou.pipeline.core.task.PipeTask;

import java.time.Duration;
import java.time.Instant;

/**
 * 任务上下文
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
public interface TaskContext<I, O> {

    PipeTask<I, O> getTask();

    TaskParameter getParameter();

    DataBuffer<I> getReadBuffer();

    DataBuffer<O> getWriteBuffer();

    Counter getReaderCounter();

    Counter getProcessorCounter();

    Counter getWriterCounter();

    TaskStateHolder getReaderState();

    TaskStateHolder getProcessorState();

    TaskStateHolder getWriterState();

    Holder<Long> getTotal();

    Holder<Instant> getStartTime();

    Holder<Instant> getFinishTime();

    Holder<Duration> getReportPeriod();

    Holder<Duration> getTimeout();

    default Duration getCost() {
        if (!getStartTime().isPresent()) {
            return Duration.ZERO;
        }
        if (!getFinishTime().isPresent()) {
            return Duration.between(getStartTime().get(), Instant.now());
        }
        return Duration.between(getStartTime().get(), getFinishTime().get());
    }

    default boolean isTimeout() {
        if (!getTimeout().isPresent()) {
            return false;
        }
        if (!getStartTime().isPresent()) {
            return false;
        }
        if (!getFinishTime().isPresent()) {
            return false;
        }
        return Duration.between(getStartTime().get(), Instant.now()).compareTo(getTimeout().get()) > 0;
    }

    default boolean isTerminated() {
        return getReaderState().get() == TaskState.TERMINATED && getProcessorState().get() == TaskState.TERMINATED && getWriterState().get() == TaskState.TERMINATED;
    }

    default boolean isFailed() {
        return getReaderState().get() == TaskState.FAILED || getProcessorState().get() == TaskState.FAILED || getWriterState().get() == TaskState.FAILED;
    }

}
