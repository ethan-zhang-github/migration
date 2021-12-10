package priv.ethanzhang.migration.core.event;

import lombok.Getter;
import priv.ethanzhang.migration.core.task.MigrationTask;

/**
 * 任务警告事件
 */
@Getter
public class TaskTaskWarnningEvent extends TaskTaskLifecycleEvent {

    private final Cause cause;

    private final Throwable throwable;

    public TaskTaskWarnningEvent(MigrationTask<?, ?> task, Cause cause, Throwable throwable) {
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
