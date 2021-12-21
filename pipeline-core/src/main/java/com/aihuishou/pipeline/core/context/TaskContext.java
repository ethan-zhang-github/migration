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

    TaskParameter getParameter();

    PipeTask<I, O> getTask();

    DataBuffer<I> getReadBuffer();

    DataBuffer<O> getWriteBuffer();

    Counter getReadCounter();

    Counter getProcessedCounter();

    Counter getWrittenCounter();

    Holder<TaskState> getReaderState();

    Holder<TaskState> getProcessorState();

    Holder<TaskState> getWriterState();

    Holder<Long> getTotal();

    Holder<Instant> getStartTimestamp();

    Holder<Instant> getFinishTimestamp();

    Duration getCost();

    Holder<Duration> getReportPeriod();

    Holder<Duration> getTimeout();

    boolean isTimeout();

    default boolean isTerminated() {
        return getReaderState().get() == TaskState.TERMINATED && getProcessorState().get() == TaskState.TERMINATED && getWriterState().get() == TaskState.TERMINATED;
    }

    default boolean isFailed() {
        return getReaderState().get() == TaskState.FAILED || getProcessorState().get() == TaskState.FAILED || getWriterState().get() == TaskState.FAILED;
    }

}
