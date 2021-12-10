package priv.ethanzhang.migration.core.event;

import priv.ethanzhang.migration.core.task.MigrationTask;

public class TaskTaskStoppedEvent extends TaskTaskLifecycleEvent {

    public TaskTaskStoppedEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
