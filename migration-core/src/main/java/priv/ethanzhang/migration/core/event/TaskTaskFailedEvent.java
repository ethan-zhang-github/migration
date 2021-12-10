package priv.ethanzhang.migration.core.event;

import lombok.Getter;
import priv.ethanzhang.migration.core.task.MigrationTask;

@Getter
public class TaskTaskFailedEvent extends TaskTaskLifecycleEvent {

    private final Cause cause;

    private final Throwable throwable;

    public TaskTaskFailedEvent(MigrationTask<?, ?> task, Cause cause, Throwable throwable) {
        super(task);
        this.cause = cause;
        this.throwable = throwable;
    }

    public enum Cause {

        READER_FAILED,
        PROCESSOR_FAILED,
        WRITER_FAILED

    }

}
