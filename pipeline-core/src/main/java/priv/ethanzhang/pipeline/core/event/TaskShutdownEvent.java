package priv.ethanzhang.pipeline.core.event;

import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 任务终止事件
 * @author ethan zhang
 */
public class TaskShutdownEvent extends TaskLifecycleEvent {

    public TaskShutdownEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
