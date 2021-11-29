package priv.ethanzhang.event;

import lombok.Getter;
import priv.ethanzhang.task.MigrationTask;

/**
 * 任务警告事件
 */
@Getter
public class MigrationTaskWarnningEvent extends MigrationTaskLifecycleEvent {

    private final Cause cause;

    public MigrationTaskWarnningEvent(MigrationTask<?, ?> task, Cause cause) {
        super(task);
        this.cause = cause;
    }

    public enum Cause {

        READER_PRODUCE_FAILED,
        PROCESSOR_PRODUCE_FAILED

    }

}
