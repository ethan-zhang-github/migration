package priv.ethanzhang.migration.core.exception;

public class MigrationTaskWriteException extends MigrationTaskException {

    public MigrationTaskWriteException(Throwable cause) {
        super(cause);
    }

    public MigrationTaskWriteException(String message) {
        super(message);
    }

}
