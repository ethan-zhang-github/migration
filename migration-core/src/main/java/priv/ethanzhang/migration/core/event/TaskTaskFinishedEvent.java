package priv.ethanzhang.migration.core.event;

import priv.ethanzhang.migration.core.task.MigrationTask;

public class TaskTaskFinishedEvent extends TaskTaskLifecycleEvent {

    public TaskTaskFinishedEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
