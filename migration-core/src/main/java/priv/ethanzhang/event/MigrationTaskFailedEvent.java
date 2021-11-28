package priv.ethanzhang.event;

import lombok.Getter;
import priv.ethanzhang.task.MigrationTask;

@Getter
public class MigrationTaskFailedEvent extends MigrationTaskLifecycleEvent {

    private final Cause cause;

    private final Throwable throwable;

    public MigrationTaskFailedEvent(MigrationTask<?, ?> task, Cause cause, Throwable throwable) {
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
