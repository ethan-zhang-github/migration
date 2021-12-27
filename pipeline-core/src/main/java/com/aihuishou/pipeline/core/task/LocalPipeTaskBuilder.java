package com.aihuishou.pipeline.core.task;

import com.aihuishou.pipeline.core.buffer.DisruptorDataBuffer;
import com.aihuishou.pipeline.core.common.LocalCounter;
import com.aihuishou.pipeline.core.common.LocalHolder;
import com.aihuishou.pipeline.core.common.LocalOnceHolder;
import com.aihuishou.pipeline.core.context.LocalTaskContext;
import com.aihuishou.pipeline.core.context.LocalTaskParameter;
import com.aihuishou.pipeline.core.context.LocalTaskStateHolder;
import com.aihuishou.pipeline.core.event.dispatcher.DisruptorTaskEventDispatcher;
import com.aihuishou.pipeline.core.executor.LocalTaskExecutor;
import com.aihuishou.pipeline.core.manager.LocalTaskManager;

import java.util.Optional;

public class LocalPipeTaskBuilder<I, O> extends AbstractPipeTaskBuilder<I, O, LocalPipeTaskBuilder<I, O>> {

    private LocalPipeTaskBuilder() {
        super(LocalTaskParameter::new, DisruptorDataBuffer::new, LocalTaskStateHolder::new);
    }

    public static <I, O> LocalPipeTaskBuilder<I, O> newBuilder() {
        return new LocalPipeTaskBuilder<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initialize(PipeTask<I, O> task) {
        task.setTaskId(Optional.ofNullable(taskId).orElse(LocalTaskIdGenerator.INSTANCE.generate()));
        task.setReader(reader);
        task.setProcessorChain(processorChain);
        task.setWriter(writer);
        task.setReporter(reporter);
        task.setDispatcher(DisruptorTaskEventDispatcher.INSTANCE);
        task.setManager(LocalTaskManager.INSTANCE);
        task.setExecutor(new LocalTaskExecutor<>(executor));
        LocalTaskContext<I, O> context = new LocalTaskContext<>();
        context.setTask(task);
        context.setParameter(parameter);
        context.setReadBuffer(dataBufferGenerator.apply(readBufferSize));
        context.setWriteBuffer(dataBufferGenerator.apply(writeBufferSize));
        context.setReaderCounter(new LocalCounter());
        context.setProcessorCounter(new LocalCounter());
        context.setWriterCounter(new LocalCounter());
        context.setReaderState(new LocalTaskStateHolder());
        context.setProcessorState(new LocalTaskStateHolder());
        context.setWriterState(new LocalTaskStateHolder());
        context.setTotal(new LocalHolder<>(total));
        context.setStartTime(new LocalOnceHolder<>());
        context.setFinishTime(new LocalOnceHolder<>());
        context.setReportPeriod(new LocalHolder<>(reportPeriod));
        context.setTimeout(new LocalHolder<>(timeout));
        task.setContext(context);
    }

}
