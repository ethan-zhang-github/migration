package priv.ethanzhang.event;

import priv.ethanzhang.task.MigrationTask;

public class MigrationTaskStoppedEvent extends MigrationTaskLifecycleEvent {

    public MigrationTaskStoppedEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
