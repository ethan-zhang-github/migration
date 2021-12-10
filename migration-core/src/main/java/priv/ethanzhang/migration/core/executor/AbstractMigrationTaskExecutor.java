package priv.ethanzhang.migration.core.executor;

import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.event.TaskTaskShutdownEvent;
import priv.ethanzhang.migration.core.event.TaskTaskStartedEvent;
import priv.ethanzhang.migration.core.event.TaskTaskStoppedEvent;
import priv.ethanzhang.migration.core.exception.TaskExecutionException;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.time.Instant;

import static priv.ethanzhang.migration.core.context.MigrationState.*;

public abstract class AbstractMigrationTaskExecutor<I, O> implements MigrationTaskExecutor<I, O> {

    @Override
    public synchronized void start(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (context.getReaderState().canRun() && context.getProcessorState().canRun() && context.getWriterState().canRun()) {
            context.setStartTimestamp(Instant.now());
            context.setReaderState(RUNNING);
            startReader(task);
            context.setProcessorState(RUNNING);
            startProcessor(task);
            context.setWriterState(RUNNING);
            startWriter(task);
            task.getDispatcher().dispatch(new TaskTaskStartedEvent(task));
        } else {
            throw new TaskExecutionException("The task can not start on this state!");
        }
    }

    @Override
    public synchronized void stop(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (context.getReaderState().canStop() && context.getProcessorState().canStop() && context.getWriterState().canStop()) {
            context.setReaderState(STOPPING);
            stopReader(task);
            context.setProcessorState(STOPPING);
            stopProcessor(task);
            context.setWriterState(STOPPING);
            stopWriter(task);
            task.getDispatcher().dispatch(new TaskTaskStoppedEvent(task));
        } else {
            throw new TaskExecutionException("The task can not stop on this state!");
        }
    }

    @Override
    public synchronized void shutDown(MigrationTask<I, O> task) {
        MigrationContext<I, O> context = task.getContext();
        if (context.getReaderState().canShutdown() && context.getProcessorState().canShutdown() && context.getWriterState().canShutdown()) {
            context.setReaderState(TERMINATED);
            shutdownReader(task);
            context.setProcessorState(TERMINATED);
            shutdownProcessor(task);
            context.setWriterState(TERMINATED);
            shutdownWriter(task);
            task.getDispatcher().dispatch(new TaskTaskShutdownEvent(task));
        }else {
            throw new TaskExecutionException("The task can not shutdown on this state!");
        }
    }

    protected void startReader(MigrationTask<I, O> task) {}

    protected void startProcessor(MigrationTask<I, O> task) {}

    protected void startWriter(MigrationTask<I, O> task) {}

    protected void stopReader(MigrationTask<I, O> task) {}

    protected void stopProcessor(MigrationTask<I, O> task) {}

    protected void stopWriter(MigrationTask<I, O> task) {}

    protected void shutdownReader(MigrationTask<I, O> task) {}

    protected void shutdownProcessor(MigrationTask<I, O> task) {}

    protected void shutdownWriter(MigrationTask<I, O> task) {}

}
