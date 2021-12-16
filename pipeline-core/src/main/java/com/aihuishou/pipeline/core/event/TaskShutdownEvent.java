package com.aihuishou.pipeline.core.event;

import com.aihuishou.pipeline.core.task.PipeTask;

/**
 * 任务终止事件
 * @author ethan zhang
 */
public class TaskShutdownEvent extends TaskLifecycleEvent {

    public TaskShutdownEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
