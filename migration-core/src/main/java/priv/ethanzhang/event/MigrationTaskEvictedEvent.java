package priv.ethanzhang.event;

import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.Getter;
import priv.ethanzhang.task.MigrationTask;

/**
 * 任务淘汰事件
 * @author ethan zhang
 */
@Getter
public class MigrationTaskEvictedEvent extends MigrationTaskLifecycleEvent {

    private final RemovalCause cause;

    public MigrationTaskEvictedEvent(MigrationTask<?, ?> task, RemovalCause cause) {
        super(task);
        this.cause = cause;
    }

}
