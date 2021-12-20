package com.aihuishou.pipeline.core.event;

import com.aihuishou.pipeline.core.task.PipeTask;

/**
 * 任务暂停事件
 * @author ethan zhang
 */
public class TaskStoppedEvent extends TaskLifecycleEvent {

    public TaskStoppedEvent(PipeTask<?, ?> task) {
        super(task);
    }

    @Override
    public String toString() {
        return String.format("TaskStoppedEvent occured, taskId: %s, timestamp: %s", task.getTaskId(), timestamp);
    }

}
