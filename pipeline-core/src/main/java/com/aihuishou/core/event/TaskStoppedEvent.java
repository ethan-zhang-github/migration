package com.aihuishou.core.event;

import com.aihuishou.core.task.PipeTask;

/**
 * 任务暂停事件
 * @author ethan zhang
 */
public class TaskStoppedEvent extends TaskLifecycleEvent {

    public TaskStoppedEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
