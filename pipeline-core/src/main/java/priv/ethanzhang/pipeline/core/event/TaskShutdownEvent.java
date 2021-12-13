package priv.ethanzhang.pipeline.core.event;

import priv.ethanzhang.pipeline.core.task.PipeTask;

public class TaskShutdownEvent extends TaskLifecycleEvent {

    public TaskShutdownEvent(PipeTask<?, ?> task) {
        super(task);
    }

}
