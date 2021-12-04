package priv.ethanzhang.migration.core.exception;

public class MigrationTaskException extends RuntimeException {

    public MigrationTaskException(Throwable cause) {
        super(cause);
    }

    public MigrationTaskException(String message) {
        super(message);
    }

    public MigrationTaskException(String message, Throwable cause) {
        super(message, cause);
    }

}
