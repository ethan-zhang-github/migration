package priv.ethanzhang.migration.core.event;

import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.Getter;
import priv.ethanzhang.migration.core.task.MigrationTask;

/**
 * 任务淘汰事件
 */
@Getter
public class TaskTaskEvictedEvent extends TaskTaskLifecycleEvent {

    private final RemovalCause cause;

    public TaskTaskEvictedEvent(MigrationTask<?, ?> task, RemovalCause cause) {
        super(task);
        this.cause = cause;
    }

}
