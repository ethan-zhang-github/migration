package priv.ethanzhang.event;

import lombok.Getter;
import priv.ethanzhang.task.MigrationTask;

@Getter
public class MigrationTaskWarnEvent extends MigrationTaskLifecycleEvent {

    private final Cause cause;

    public MigrationTaskWarnEvent(MigrationTask<?, ?> task, Cause cause) {
        super(task);
        this.cause = cause;
    }

    public enum Cause {

        READER_PRODUCE_FAILED,
        PROCESSOR_PRODUCE_FAILED

    }

}
