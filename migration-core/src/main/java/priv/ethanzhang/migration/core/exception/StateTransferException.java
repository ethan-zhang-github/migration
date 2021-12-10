package priv.ethanzhang.migration.core.exception;

import lombok.Getter;
import priv.ethanzhang.migration.core.context.MigrationState;

@Getter
public class StateTransferException extends TaskException {

    private final MigrationState origin;

    private final MigrationState target;

    public StateTransferException(MigrationState origin, MigrationState target) {
        super(String.format("origin state %s can not transfer to target state %s !", origin, target));
        this.origin = origin;
        this.target = target;
    }

}
