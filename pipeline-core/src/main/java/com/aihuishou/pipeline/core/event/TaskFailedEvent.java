package com.aihuishou.pipeline.core.event;

import lombok.Getter;
import com.aihuishou.pipeline.core.task.PipeTask;

/**
 * 任务失败事件
 * @author ethan zhang
 */
@Getter
public class TaskFailedEvent extends TaskLifecycleEvent {

    private final Cause cause;

    private final Throwable throwable;

    public TaskFailedEvent(PipeTask<?, ?> task, Cause cause, Throwable throwable) {
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
