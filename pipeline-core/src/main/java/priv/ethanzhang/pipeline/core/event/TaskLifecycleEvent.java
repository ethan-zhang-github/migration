package priv.ethanzhang.pipeline.core.event;

import lombok.Getter;
import priv.ethanzhang.pipeline.core.task.PipeTask;

import java.time.Instant;

/**
 * 任务生命周期事件
 * @author ethan zhang
 */
@Getter
public abstract class TaskLifecycleEvent implements TaskEvent {

    private final Instant timestamp = Instant.now();

    private final PipeTask<?, ?> task;

    public TaskLifecycleEvent(PipeTask<?, ?> task) {
        this.task = task;
    }

}
