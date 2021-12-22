package com.aihuishou.pipeline.core.task;

import com.aihuishou.pipeline.core.config.GlobalConfig;
import com.aihuishou.pipeline.core.context.LocalTaskContext;
import com.aihuishou.pipeline.core.context.LocalTaskParameter;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.executor.LocalTaskExecutor;
import com.aihuishou.pipeline.core.executor.TaskExecutor;
import com.aihuishou.pipeline.core.manager.LocalTaskManager;

import java.util.UUID;

public class LocalPipeTaskBuilder<I, O> extends AbstractPipeTaskBuilder<I, O, LocalPipeTaskBuilder<I, O>> {

    private LocalPipeTaskBuilder() {
        super(LocalTaskParameter::new,
                GlobalConfig.BUFFER.getDefaultDataBuffer(),
                () -> LocalTaskManager.INSTANCE,
                GlobalConfig.LOCAL_DISPATCHER.getDefaultDispatcher(),
                () -> UUID.randomUUID().toString());
    }

    public static <I, O> LocalPipeTaskBuilder<I, O> newBuilder() {
        return new LocalPipeTaskBuilder<>();
    }

    @Override
    protected TaskExecutor<I, O> generateTaskExecutor() {
        return new LocalTaskExecutor<I, O>(executor);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TaskContext<I, O> buildContext(PipeTask<I, O> task) {
        return LocalTaskContext.<I, O>builder()
                .task(task)
                .parameter(parameter)
                .readBuffer(dataBufferGenerator.apply(readBufferSize))
                .writeBuffer(dataBufferGenerator.apply(writeBufferSize))
                .build();
    }

}
