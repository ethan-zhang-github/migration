package priv.ethanzhang.event;

import lombok.Getter;
import priv.ethanzhang.task.MigrationTask;

import java.time.Instant;

/**
 * 任务生命周期事件
 * @author ethan zhang
 */
@Getter
public abstract class MigrationTaskLifecycleEvent {

    private final Instant timestamp = Instant.now();

    private final MigrationTask<?, ?> task;

    public MigrationTaskLifecycleEvent(MigrationTask<?, ?> task) {
        this.task = task;
    }

}
