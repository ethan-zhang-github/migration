package priv.ethanzhang.pipeline.core.event;

import priv.ethanzhang.pipeline.core.task.PipeTask;

public class TaskStoppedEvent extends TaskLifecycleEvent {

    public TaskStoppedEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
