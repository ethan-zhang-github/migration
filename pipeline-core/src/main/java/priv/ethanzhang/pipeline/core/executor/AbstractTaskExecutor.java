package priv.ethanzhang.pipeline.core.executor;

import lombok.Getter;
import priv.ethanzhang.pipeline.core.context.TaskContext;
import priv.ethanzhang.pipeline.core.event.TaskShutdownEvent;
import priv.ethanzhang.pipeline.core.event.TaskStartedEvent;
import priv.ethanzhang.pipeline.core.event.TaskStoppedEvent;
import priv.ethanzhang.pipeline.core.exception.TaskExecutionException;
import priv.ethanzhang.pipeline.core.task.PipeTask;

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
        if (context.getReaderState().canRun() && context.getProcessorState().canRun() && context.getWriterState().canRun()) {
            context.setStartTimestamp(Instant.now());
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
        if (context.getReaderState().canStop() && context.getProcessorState().canStop() && context.getWriterState().canStop()) {
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
        if (context.getReaderState().canShutdown() && context.getProcessorState().canShutdown() && context.getWriterState().canShutdown()) {
            readerExecutor.shutDown(task, task.getReader());
            processorExecutor.shutDown(task, task.getProcessorChain());
            writerExecutor.shutDown(task, task.getWriter());
            task.getDispatcher().dispatch(new TaskShutdownEvent(task));
        }else {
            throw new TaskExecutionException("The task can not shutdown on this state!");
        }
    }

}
