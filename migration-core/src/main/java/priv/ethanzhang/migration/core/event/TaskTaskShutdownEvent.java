package priv.ethanzhang.migration.core.event;

import priv.ethanzhang.migration.core.task.MigrationTask;

public class TaskTaskShutdownEvent extends TaskTaskLifecycleEvent {

    public TaskTaskShutdownEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
