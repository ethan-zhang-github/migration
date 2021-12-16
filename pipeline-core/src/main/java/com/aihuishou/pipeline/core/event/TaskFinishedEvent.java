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

}
