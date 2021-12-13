package priv.ethanzhang.pipeline.core.event;

import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 任务开始事件
 * @author ethan zhang
 */
public class TaskStartedEvent extends TaskLifecycleEvent {

    public TaskStartedEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
