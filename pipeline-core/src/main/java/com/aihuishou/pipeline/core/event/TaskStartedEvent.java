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

}
