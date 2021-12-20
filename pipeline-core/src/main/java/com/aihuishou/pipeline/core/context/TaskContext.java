package com.aihuishou.pipeline.core.context;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
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

    LocalTaskParameter getParameter();

    PipeTask<I, O> getTask();

    DataBuffer<I> getReadBuffer();

    DataBuffer<O> getWriteBuffer();

    long getReadCount();

    void incrReadCount(long count);

    long getProcessedCount();

    void incrProcessedCount(long count);

    long getWrittenCount();

    void incrWrittenCount(long count);

    TaskState getReaderState();

    TaskState getProcessorState();

    TaskState getWriterState();

    void setReaderState(TaskState state);

    void setProcessorState(TaskState state);

    void setWriterState(TaskState state);

    long getTotal();

    void setTotal(long total);

    Instant getStartTimestamp();

    void setStartTimestamp(Instant instant);

    Instant getFinishTimestamp();

    void setFinishTimestamp(Instant instant);

    Duration getCost();

    void setReportPeriod(Duration reportPeriod);

    Duration getReportPeriod();

    void setTimeout(Duration timeout);

    Duration getTimeout();

    boolean isTimeout();

    default boolean isTerminated() {
        return getReaderState() == TaskState.TERMINATED && getProcessorState() == TaskState.TERMINATED && getWriterState() == TaskState.TERMINATED;
    }

    default boolean isFailed() {
        return getReaderState() == TaskState.FAILED || getProcessorState() == TaskState.FAILED || getWriterState() == TaskState.FAILED;
    }

}
