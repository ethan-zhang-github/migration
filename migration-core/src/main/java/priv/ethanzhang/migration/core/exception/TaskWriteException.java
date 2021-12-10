package priv.ethanzhang.migration.core.exception;

public class TaskWriteException extends TaskException {

    public TaskWriteException(Throwable cause) {
        super(cause);
    }

    public TaskWriteException(String message) {
        super(message);
    }

}
