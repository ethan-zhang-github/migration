package priv.ethanzhang.pipeline.core.executor;

import priv.ethanzhang.pipeline.core.context.TaskContext;
import priv.ethanzhang.pipeline.core.context.TaskState;
import priv.ethanzhang.pipeline.core.event.TaskShutdownEvent;
import priv.ethanzhang.pipeline.core.event.TaskStartedEvent;
import priv.ethanzhang.pipeline.core.event.TaskStoppedEvent;
import priv.ethanzhang.pipeline.core.exception.TaskExecutionException;
import priv.ethanzhang.pipeline.core.task.PipeTask;

import java.time.Instant;

public abstract class AbstractTaskExecutor<I, O> implements TaskExecutor<I, O> {

    @Override
    public synchronized void start(PipeTask<I, O> task) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().canRun() && context.getProcessorState().canRun() && context.getWriterState().canRun()) {
            context.setStartTimestamp(Instant.now());
            context.setReaderState(TaskState.RUNNING);
            startReader(task);
            context.setProcessorState(TaskState.RUNNING);
            startProcessor(task);
            context.setWriterState(TaskState.RUNNING);
            startWriter(task);
            task.getDispatcher().dispatch(new TaskStartedEvent(task));
        } else {
            throw new TaskExecutionException("The task can not start on this state!");
        }
    }

    @Override
    public synchronized void stop(PipeTask<I, O> task) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().canStop() && context.getProcessorState().canStop() && context.getWriterState().canStop()) {
            context.setReaderState(TaskState.STOPPING);
            stopReader(task);
            context.setProcessorState(TaskState.STOPPING);
            stopProcessor(task);
            context.setWriterState(TaskState.STOPPING);
            stopWriter(task);
            task.getDispatcher().dispatch(new TaskStoppedEvent(task));
        } else {
            throw new TaskExecutionException("The task can not stop on this state!");
        }
    }

    @Override
    public synchronized void shutDown(PipeTask<I, O> task) {
        TaskContext<I, O> context = task.getContext();
        if (context.getReaderState().canShutdown() && context.getProcessorState().canShutdown() && context.getWriterState().canShutdown()) {
            context.setReaderState(TaskState.TERMINATED);
            shutdownReader(task);
            context.setProcessorState(TaskState.TERMINATED);
            shutdownProcessor(task);
            context.setWriterState(TaskState.TERMINATED);
            shutdownWriter(task);
            task.getDispatcher().dispatch(new TaskShutdownEvent(task));
        }else {
            throw new TaskExecutionException("The task can not shutdown on this state!");
        }
    }

    protected void startReader(PipeTask<I, O> task) {}

    protected void startProcessor(PipeTask<I, O> task) {}

    protected void startWriter(PipeTask<I, O> task) {}

    protected void stopReader(PipeTask<I, O> task) {}

    protected void stopProcessor(PipeTask<I, O> task) {}

    protected void stopWriter(PipeTask<I, O> task) {}

    protected void shutdownReader(PipeTask<I, O> task) {}

    protected void shutdownProcessor(PipeTask<I, O> task) {}

    protected void shutdownWriter(PipeTask<I, O> task) {}

}
