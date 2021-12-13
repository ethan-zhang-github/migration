package priv.ethanzhang.pipeline.core.event;

import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 任务完成事件
 * @author ethanzhang
 */
public class TaskFinishedEvent extends TaskLifecycleEvent {

    public TaskFinishedEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
