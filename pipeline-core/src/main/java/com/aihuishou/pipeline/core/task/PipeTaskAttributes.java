package com.aihuishou.pipeline.core.task;

import com.aihuishou.pipeline.core.context.LocalTaskParameter;
import com.aihuishou.pipeline.core.utils.DateTimeFormatters;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class PipeTaskAttributes {

    private final EnumMap<AttributeType, Object> attributes = new EnumMap<>(AttributeType.class);

    public static PipeTaskAttributes of(PipeTask<?, ?> task) {
        PipeTaskAttributes attributes = new PipeTaskAttributes();
        attributes.set(AttributeType.TASK_ID, task.getTaskId());
        attributes.set(AttributeType.READER_TYPE, task.getReader().getClass());
        attributes.set(AttributeType.PROCESSOR_TYPE, task.getProcessorChain().getProcessorTypes());
        attributes.set(AttributeType.WRITER_TYPE, task.getWriter().getClass());
        attributes.set(AttributeType.PARAMETER, task.getContext().getParameter());
        attributes.set(AttributeType.READER_BUFFER_SIZE, task.getContext().getReadBuffer().size());
        attributes.set(AttributeType.READER_BUFFER_CAPACITY, task.getContext().getReadBuffer().capacity());
        attributes.set(AttributeType.WRITER_BUFFER_SIZE, task.getContext().getWriteBuffer().size());
        attributes.set(AttributeType.WRITER_BUFFER_CAPACITY, task.getContext().getWriteBuffer().capacity());
        attributes.set(AttributeType.READ_COUNT, task.getContext().getReadCount());
        attributes.set(AttributeType.PROCESSED_COUNT, task.getContext().getProcessedCount());
        attributes.set(AttributeType.WRITTEN_COUNT, task.getContext().getWrittenCount());
        attributes.set(AttributeType.READER_STATE, task.getContext().getReaderState());
        attributes.set(AttributeType.PROCESSOR_STATE, task.getContext().getProcessorState());
        attributes.set(AttributeType.WRITER_STATE, task.getContext().getWriterState());
        attributes.set(AttributeType.EVENT_STREAM, task.getDispatcher().getTaskEventStream(task.getTaskId()));
        attributes.set(AttributeType.TOTAL, task.getContext().getTotal());
        attributes.set(AttributeType.START_TIME, task.getContext().getStartTimestamp());
        attributes.set(AttributeType.FINISH_TIME, task.getContext().getFinishTimestamp());
        attributes.set(AttributeType.COST, task.getContext().getCost());
        attributes.set(AttributeType.TIMEOUT, task.getContext().getTimeout());
        return attributes;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(AttributeType attributeType) {
        return (T) attributes.get(attributeType);
    }

    public void set(AttributeType attributeType, Object val) {
        attributes.put(attributeType, val);
    }

    public String format(String separator) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("TASK_ID: [%s]%s", get(PipeTaskAttributes.AttributeType.TASK_ID), separator));
        builder.append(String.format("READER_TYPE: [%s]%s", get(AttributeType.READER_TYPE), separator));
        List<Class<?>> processorTypes = get(AttributeType.PROCESSOR_TYPE);
        builder.append(String.format("PROCESSOR_TYPE: [%s]%s", processorTypes.stream().map(Class::toString).collect(Collectors.joining(" -> ")), separator));
        builder.append(String.format("WRITER_TYPE: [%s]%s", get(AttributeType.WRITER_TYPE), separator));
        LocalTaskParameter parameter = get(AttributeType.PARAMETER);
        if (parameter != null) {
            builder.append(String.format("PARAMETER: [%s]%s", parameter, separator));
        }
        long total = get(AttributeType.TOTAL);
        if (total > 0) {
            builder.append(String.format("PARAMETER: [%s]%s", total, separator));
        }
        int readerBufferCapacity = get(AttributeType.READER_BUFFER_CAPACITY);
        if (readerBufferCapacity > 0) {
            builder.append(String.format("READER_BUFFER_SIZE: [%s/%s]%s", get(AttributeType.READER_BUFFER_SIZE), readerBufferCapacity, separator));
        } else {
            builder.append(String.format("READER_BUFFER_SIZE: [%s]%s", get(AttributeType.READER_BUFFER_SIZE), separator));
        }
        int writerBufferCapacity = get(AttributeType.WRITER_BUFFER_CAPACITY);
        if (writerBufferCapacity > 0) {
            builder.append(String.format("WRITER_BUFFER_SIZE: [%s/%s]%s", get(AttributeType.WRITER_BUFFER_SIZE), writerBufferCapacity, separator));
        } else {
            builder.append(String.format("WRITER_BUFFER_SIZE: [%s]%s", get(AttributeType.WRITER_BUFFER_SIZE), separator));
        }
        builder.append(String.format("READER_STATE: [%s(%s)]%s", get(AttributeType.READER_STATE), get(AttributeType.READ_COUNT), separator));
        builder.append(String.format("PROCESSOR_STATE: [%s(%s)]%s", get(AttributeType.PROCESSOR_STATE), get(AttributeType.PROCESSED_COUNT), separator));
        builder.append(String.format("WRITER_STATE: [%s(%s)]%s", get(AttributeType.WRITER_STATE), get(AttributeType.WRITTEN_COUNT), separator));
        Instant startTime = get(AttributeType.START_TIME);
        if (startTime != null) {
            builder.append(String.format("START_TIME: [%s]%s", DateTimeFormatters.PATTERN_0.formatInstant(startTime), separator));
        }
        Instant finishTime = get(AttributeType.FINISH_TIME);
        if (finishTime != null) {
            builder.append(String.format("FINISH_TIME: [%s]%s", DateTimeFormatters.PATTERN_0.formatInstant(finishTime), separator));
        }
        builder.append(String.format("COST: [%s]%s", get(AttributeType.COST), separator));
        builder.append(String.format("TIMEOUT: [%s]%s", get(AttributeType.TIMEOUT), separator));
        return builder.toString();
    }

    public enum AttributeType {

        TASK_ID,
        READER_TYPE,
        PROCESSOR_TYPE,
        WRITER_TYPE,
        PARAMETER,
        READER_BUFFER_SIZE,
        READER_BUFFER_CAPACITY,
        WRITER_BUFFER_SIZE,
        WRITER_BUFFER_CAPACITY,
        READ_COUNT,
        PROCESSED_COUNT,
        WRITTEN_COUNT,
        READER_STATE,
        PROCESSOR_STATE,
        WRITER_STATE,
        EVENT_STREAM,
        TOTAL,
        START_TIME,
        FINISH_TIME,
        COST,
        TIMEOUT;

    }

}
