package priv.ethanzhang.migration.core.event;

import priv.ethanzhang.migration.core.task.MigrationTask;

public class MigrationTaskFinishedEvent extends MigrationTaskLifecycleEvent {

    public MigrationTaskFinishedEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
