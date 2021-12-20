package com.aihuishou.pipeline.core.exception;

public class TaskExecutionException extends TaskException {

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskExecutionException(String message) {
        super(message);
    }

}
