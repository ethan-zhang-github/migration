package com.aihuishou.pipeline.core.event;

import com.aihuishou.pipeline.core.task.PipeTask;

/**
 * 任务完成事件
 * @author ethanzhang
 */
public class TaskFinishedEvent extends TaskLifecycleEvent {

    public TaskFinishedEvent(PipeTask<?, ?> task) {
        super(task);
    }

    @Override
    public String toString() {
        return String.format("TaskFinishedEvent occured, taskId: %s, timestamp: %s", task.getTaskId(), timestamp);
    }

}
