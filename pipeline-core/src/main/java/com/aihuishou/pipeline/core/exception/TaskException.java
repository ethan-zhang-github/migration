package com.aihuishou.pipeline.core.exception;

public class TaskException extends RuntimeException {

    public TaskException(Throwable cause) {
        super(cause);
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

}
