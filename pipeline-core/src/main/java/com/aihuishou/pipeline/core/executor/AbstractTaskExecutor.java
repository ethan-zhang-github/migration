package com.aihuishou.pipeline.core.executor;

import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.event.TaskShutdownEvent;
import com.aihuishou.pipeline.core.event.TaskStartedEvent;
import com.aihuishou.pipeline.core.event.TaskStoppedEvent;
import com.aihuishou.pipeline.core.exception.TaskExecutionException;
import com.aihuishou.pipeline.core.task.PipeTask;
import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class AbstractTaskExecutor<I, O> implements TaskExecutor<I, O> {

    private final ReaderExecutor<I, O> readerExecutor;

    private final ProcessorExecutor<I, O> processorExecutor;

    private final WriterExecutor<I, O> writerExecutor;

    protected AbstractTaskExecutor(ReaderExecutor<I, O> readerExecutor, ProcessorExecutor<I, O> processorExecutor, WriterExecutor<I, O> writerExecutor) {
        this.readerExecutor = readerExecutor;
        this.processorExecutor = processorExecutor;
        this.writerExecutor = writerExecutor;
    }

    @Override
    public synchronized void start(PipeTask<I, O> task) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().get().canRun() && context.getProcessorState().get().canRun() && context.getWriterState().get().canRun()) {
            context.getStartTime().set(Instant.now());
            readerExecutor.start(task, task.getReader());
            processorExecutor.start(task, task.getProcessorChain());
            writerExecutor.start(task, task.getWriter());
            task.getDispatcher().dispatch(new TaskStartedEvent(task));
        } else {
            throw new TaskExecutionException("The task can not start on this state!");
        }
    }

    @Override
    public synchronized void stop(PipeTask<I, O> task) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().get().canStop() && context.getProcessorState().get().canStop() && context.getWriterState().get().canStop()) {
            readerExecutor.stop(task, task.getReader());
            processorExecutor.stop(task, task.getProcessorChain());
            writerExecutor.stop(task, task.getWriter());
            task.getDispatcher().dispatch(new TaskStoppedEvent(task));
        } else {
            throw new TaskExecutionException("The task can not stop on this state!");
        }
    }

    @Override
    public synchronized void shutDown(PipeTask<I, O> task) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().get().canShutdown() && context.getProcessorState().get().canShutdown() && context.getWriterState().get().canShutdown()) {
            readerExecutor.shutDown(task, task.getReader());
            processorExecutor.shutDown(task, task.getProcessorChain());
            writerExecutor.shutDown(task, task.getWriter());
            task.getDispatcher().dispatch(new TaskShutdownEvent(task));
        }else {
            throw new TaskExecutionException("The task can not shutdown on this state!");
        }
    }

    @Override
    public void join(PipeTask<I, O> task) {
        readerExecutor.join(task, task.getReader());
        processorExecutor.join(task, task.getProcessorChain());
        writerExecutor.join(task, task.getWriter());
    }

}
