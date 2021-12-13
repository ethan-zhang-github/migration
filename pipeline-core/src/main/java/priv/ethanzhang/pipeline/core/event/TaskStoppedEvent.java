package priv.ethanzhang.pipeline.core.event;

import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 任务暂停事件
 * @author ethan zhang
 */
public class TaskStoppedEvent extends TaskLifecycleEvent {

    public TaskStoppedEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
