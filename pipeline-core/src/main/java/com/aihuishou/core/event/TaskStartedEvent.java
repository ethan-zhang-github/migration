package com.aihuishou.core.event;

import com.aihuishou.core.task.PipeTask;

/**
 * 任务开始事件
 * @author ethan zhang
 */
public class TaskStartedEvent extends TaskLifecycleEvent {

    public TaskStartedEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
