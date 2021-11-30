package priv.ethanzhang.migration.core.executor;

import priv.ethanzhang.migration.core.event.MigrationTaskShutdownEvent;
import priv.ethanzhang.migration.core.event.MigrationTaskStartedEvent;
import priv.ethanzhang.migration.core.event.MigrationTaskStoppedEvent;
import priv.ethanzhang.migration.core.task.MigrationTask;

public abstract class AbstractMigrationTaskExecutor<I, O> implements MigrationTaskExecutor<I, O> {

    @Override
    public void execute(MigrationTask<I, O> task) {
        executeReader(task);
        executeProcessor(task);
        executeWriter(task);
        task.getDispatcher().dispatch(new MigrationTaskStartedEvent(task));
    }

    @Override
    public void stop(MigrationTask<I, O> task) {
        stopReader(task);
        stopProcessor(task);
        stopWriter(task);
        task.getDispatcher().dispatch(new MigrationTaskStoppedEvent(task));
    }

    @Override
    public void shutDown(MigrationTask<I, O> task) {
        shutdownReader(task);
        shutdownProcessor(task);
        shutdownWriter(task);
        task.getDispatcher().dispatch(new MigrationTaskShutdownEvent(task));
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
