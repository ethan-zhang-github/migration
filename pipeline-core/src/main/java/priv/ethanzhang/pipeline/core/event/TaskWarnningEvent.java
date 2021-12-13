package priv.ethanzhang.pipeline.core.event;

import lombok.Getter;
import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 任务警告事件
 */
@Getter
public class TaskWarnningEvent extends TaskLifecycleEvent {

    private final Cause cause;

    private final Throwable throwable;

    public TaskWarnningEvent(PipeTask<?, ?> task, Cause cause, Throwable throwable) {
        super(task);
        this.cause = cause;
        this.throwable = throwable;
    }

    public enum Cause {

        READER_FAILED,
        READER_TO_BUFFER_FAILED,
        PROCESSOR_FAILED,
        PROCESSOR_TO_BUFFER_FAILED,
        WRITER_FAILED

    }

}
