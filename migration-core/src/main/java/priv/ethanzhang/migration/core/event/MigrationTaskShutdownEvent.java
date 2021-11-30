package priv.ethanzhang.migration.core.event;

import priv.ethanzhang.migration.core.task.MigrationTask;

public class MigrationTaskShutdownEvent extends MigrationTaskLifecycleEvent {

    public MigrationTaskShutdownEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
