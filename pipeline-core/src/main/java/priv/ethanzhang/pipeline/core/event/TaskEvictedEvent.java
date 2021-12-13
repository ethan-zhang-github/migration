package priv.ethanzhang.pipeline.core.event;

import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.Getter;
import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 任务淘汰事件
 * @author ethan zhang
 */
@Getter
public class TaskEvictedEvent extends TaskLifecycleEvent {

    private final RemovalCause cause;

    public TaskEvictedEvent(PipeTask<?, ?> task, RemovalCause cause) {
        super(task);
        this.cause = cause;
    }

}
