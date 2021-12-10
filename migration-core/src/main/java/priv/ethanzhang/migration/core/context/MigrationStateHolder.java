package priv.ethanzhang.migration.core.context;

import priv.ethanzhang.migration.core.exception.StateTransferException;

import java.util.concurrent.atomic.AtomicReference;

public class MigrationStateHolder {

    private final AtomicReference<MigrationState> state = new AtomicReference<>(MigrationState.NEW);

    public MigrationState get() {
        return state.get();
    }

    public void transfer(MigrationState target) {
        MigrationState origin = state.get();
        if (origin == target) {
            return;
        }
        switch (origin) {
            case NEW:
                if (target != MigrationState.RUNNING) {
                    throw new StateTransferException(origin, target);
                }
                break;
            case RUNNING:
                if (target == MigrationState.NEW) {
                    throw new StateTransferException(origin, target);
                }
                break;
            case STOPPING:
                if (target != MigrationState.RUNNING && target != MigrationState.TERMINATED) {
                    throw new StateTransferException(origin, target);
                }
                break;
            case FAILED: case TERMINATED:
                throw new StateTransferException(origin, target);
            default:
                break;
        }
        if (!state.compareAndSet(origin, target)) {
            transfer(target);
        }
    }

}
