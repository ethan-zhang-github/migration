package com.aihuishou.pipeline.core.event;

import com.aihuishou.pipeline.core.task.PipeTask;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.Getter;

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

    @Override
    public String toString() {
        return String.format("TaskEvictedEvent occured, taskId: %s, timestamp: %s, cause: %s", task.getTaskId(), timestamp, cause);
    }

}
