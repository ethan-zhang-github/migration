package priv.ethanzhang.executor;

import priv.ethanzhang.event.MigrationTaskShutdownEvent;
import priv.ethanzhang.event.MigrationTaskStartedEvent;
import priv.ethanzhang.event.MigrationTaskStoppedEvent;
import priv.ethanzhang.task.MigrationTask;
import priv.ethanzhang.task.MigrationTaskListener;

import java.util.List;

public abstract class AbstractMigrationTaskExecutor<I, O> implements MigrationTaskExecutor<I, O> {

    @Override
    public void execute(MigrationTask<I, O> task) {
        List<MigrationTaskListener> listeners = task.getListeners();
        listeners.forEach(listener -> listener.beforeTaskExecute(task));
        executeReader(task);
        executeProcessor(task);
        executeWriter(task);
        listeners.forEach(listener -> listener.afterTaskExecute(task));
        task.getManager().publishEvent(new MigrationTaskStartedEvent(task));
    }

    @Override
    public void stop(MigrationTask<I, O> task) {
        List<MigrationTaskListener> listeners = task.getListeners();
        listeners.forEach(listener -> listener.beforeTaskStop(task));
        stopReader(task);
        stopProcessor(task);
        stopWriter(task);
        listeners.forEach(listener -> listener.afterTaskStop(task));
        task.getManager().publishEvent(new MigrationTaskStoppedEvent(task));
    }

    @Override
    public void shutDown(MigrationTask<I, O> task) {
        List<MigrationTaskListener> listeners = task.getListeners();
        listeners.forEach(listener -> listener.beforeTaskShutdown(task));
        shutdownReader(task);
        shutdownProcessor(task);
        shutdownWriter(task);
        listeners.forEach(listener -> listener.afterTaskShutdown(task));
        task.getManager().publishEvent(new MigrationTaskShutdownEvent(task));
    }

    protected abstract void executeReader(MigrationTask<I, O> task);

    protected abstract void executeProcessor(MigrationTask<I, O> task);

    protected abstract void executeWriter(MigrationTask<I, O> task);

    protected abstract void stopReader(MigrationTask<I, O> task);

    protected abstract void stopProcessor(MigrationTask<I, O> task);

    protected abstract void stopWriter(MigrationTask<I, O> task);

    protected abstract void shutdownReader(MigrationTask<I, O> task);

    protected abstract void shutdownProcessor(MigrationTask<I, O> task);

    protected abstract void shutdownWriter(MigrationTask<I, O> task);

}
