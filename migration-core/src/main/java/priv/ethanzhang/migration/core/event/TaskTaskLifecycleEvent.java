package priv.ethanzhang.migration.core.event;

import lombok.Getter;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.time.Instant;

/**
 * 任务生命周期事件
 * @author ethan zhang
 */
@Getter
public abstract class TaskTaskLifecycleEvent implements TaskEvent {

    private final Instant timestamp = Instant.now();

    private final MigrationTask<?, ?> task;

    public TaskTaskLifecycleEvent(MigrationTask<?, ?> task) {
        this.task = task;
    }

}
