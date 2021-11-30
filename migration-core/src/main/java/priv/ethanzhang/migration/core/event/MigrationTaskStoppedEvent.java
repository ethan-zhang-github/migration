package priv.ethanzhang.migration.core.event;

import priv.ethanzhang.migration.core.task.MigrationTask;

public class MigrationTaskStoppedEvent extends MigrationTaskLifecycleEvent {

    public MigrationTaskStoppedEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
