package com.aihuishou.pipeline.core.event;

import com.aihuishou.pipeline.core.task.PipeTask;

/**
 * 任务开始事件
 * @author ethan zhang
 */
public class TaskStartedEvent extends TaskLifecycleEvent {

    public TaskStartedEvent(PipeTask<?, ?> task) {
        super(task);
    }

    @Override
    public String toString() {
        return String.format("TaskStartedEvent occured, taskId: %s, timestamp: %s", task.getTaskId(), timestamp);
    }

}
