package com.aihuishou.core.exception;

import com.aihuishou.core.context.TaskState;
import lombok.Getter;

@Getter
public class StateTransferException extends TaskException {

    private final TaskState origin;

    private final TaskState target;

    public StateTransferException(TaskState origin, TaskState target) {
        super(String.format("origin state %s can not transfer to target state %s !", origin, target));
        this.origin = origin;
        this.target = target;
    }

}
