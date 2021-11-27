package priv.ethanzhang.event;

import priv.ethanzhang.task.MigrationTask;

public class MigrationTaskShutdownEvent extends MigrationTaskLifecycleEvent {

    public MigrationTaskShutdownEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
