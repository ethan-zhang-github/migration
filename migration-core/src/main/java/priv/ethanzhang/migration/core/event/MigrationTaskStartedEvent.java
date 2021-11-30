package priv.ethanzhang.migration.core.event;

import priv.ethanzhang.migration.core.task.MigrationTask;

/**
 * 任务开始事件
 * @author ethan zhang
 */
public class MigrationTaskStartedEvent extends MigrationTaskLifecycleEvent {

    public MigrationTaskStartedEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
