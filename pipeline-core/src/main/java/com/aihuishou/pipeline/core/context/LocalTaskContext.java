package com.aihuishou.pipeline.core.context;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.task.PipeTask;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

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

    private final LongAdder readerCounter = new LongAdder();

    private final LongAdder processorCounter = new LongAdder();

    private final LongAdder writerCounter = new LongAdder();

    private final TaskStateHolder readerState = new TaskStateHolder();

    private final TaskStateHolder processorState = new TaskStateHolder();

    private final TaskStateHolder writerState = new TaskStateHolder();

    private final AtomicLong totalCounter = new AtomicLong();

    private final AtomicReference<Instant> startTimestamap = new AtomicReference<>();

    private final AtomicReference<Instant> finishTimestamap = new AtomicReference<>();

    private Duration reportPeriod;

    private Duration timeout;

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
    public long getReadCount() {
        return readerCounter.longValue();
    }

    @Override
    public void incrReadCount(long count) {
        readerCounter.add(count);
    }

    @Override
    public long getProcessedCount() {
        return processorCounter.longValue();
    }

    @Override
    public void incrProcessedCount(long count) {
        processorCounter.add(count);
    }

    @Override
    public long getWrittenCount() {
        return writerCounter.longValue();
    }

    @Override
    public void incrWrittenCount(long count) {
        writerCounter.add(count);
    }

    @Override
    public TaskState getReaderState() {
        return readerState.get();
    }

    @Override
    public TaskState getProcessorState() {
        return processorState.get();
    }

    @Override
    public TaskState getWriterState() {
        return writerState.get();
    }

    @Override
    public void setReaderState(TaskState state) {
        readerState.transfer(state);
    }

    @Override
    public void setProcessorState(TaskState state) {
        processorState.transfer(state);
    }

    @Override
    public void setWriterState(TaskState state) {
        writerState.transfer(state);
    }

    @Override
    public long getTotal() {
        return totalCounter.get();
    }

    @Override
    public void setTotal(long total) {
        totalCounter.set(total);
    }

    @Override
    public Instant getStartTimestamp() {
        return startTimestamap.get();
    }

    @Override
    public void setStartTimestamp(Instant instant) {
        startTimestamap.compareAndSet(null, instant);
    }

    @Override
    public Instant getFinishTimestamp() {
        return finishTimestamap.get();
    }

    @Override
    public void setFinishTimestamp(Instant instant) {
        finishTimestamap.compareAndSet(null, instant);
    }

    @Override
    public Duration getCost() {
        if (startTimestamap.get() == null) {
            return Duration.ZERO;
        }
        if (finishTimestamap.get() == null) {
            return Duration.between(startTimestamap.get(), Instant.now());
        }
        return Duration.between(startTimestamap.get(), finishTimestamap.get());
    }

    @Override
    public void setReportPeriod(Duration reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    @Override
    public Duration getReportPeriod() {
        return reportPeriod;
    }

    @Override
    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    @Override
    public Duration getTimeout() {
        return timeout;
    }

    @Override
    public boolean isTimeout() {
        if (startTimestamap.get() == null) {
            return false;
        }
        if (finishTimestamap.get() != null) {
            return false;
        }
        return Duration.between(startTimestamap.get(), Instant.now()).compareTo(timeout) > 0;
    }

}
