package priv.ethanzhang.event;

import priv.ethanzhang.task.MigrationTask;

/**
 * 任务开始事件
 * @author ethan zhang
 */
public class MigrationTaskStartedEvent extends MigrationTaskLifecycleEvent {

    public MigrationTaskStartedEvent(MigrationTask<?, ?> task) {
        super(task);
    }

}
