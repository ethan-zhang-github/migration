package priv.ethanzhang.exception;

import lombok.Getter;
import priv.ethanzhang.context.MigrationState;

@Getter
public class MigrationStateTransferException extends MigrationTaskException {

    private final MigrationState origin;

    private final MigrationState target;

    public MigrationStateTransferException(MigrationState origin, MigrationState target) {
        super(String.format("origin state %s can not transfer to target state %s !", origin, target));
        this.origin = origin;
        this.target = target;
    }

}
