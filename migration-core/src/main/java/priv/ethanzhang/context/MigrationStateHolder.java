package priv.ethanzhang.context;

import priv.ethanzhang.exception.MigrationStateTransferException;

import java.util.concurrent.atomic.AtomicReference;

public class MigrationStateHolder {

    private final AtomicReference<MigrationState> state = new AtomicReference<>(MigrationState.NEW);

    public MigrationState get() {
        return state.get();
    }

    public void transfer(MigrationState target) {
        MigrationState origin = state.get();
        switch (origin) {
            case NEW:
                if (target != MigrationState.RUNNING) {
                    throw new MigrationStateTransferException(origin, target);
                }
                break;
            case RUNNING:
                if (target == MigrationState.NEW) {
                    throw new MigrationStateTransferException(origin, target);
                }
                break;
            case STOPPING:
                if (target != MigrationState.RUNNING && target != MigrationState.TERMINATED) {
                    throw new MigrationStateTransferException(origin, target);
                }
                break;
            case FAILED: case TERMINATED:
                throw new MigrationStateTransferException(origin, target);
            default:
                break;
        }
        if (!state.compareAndSet(origin, target)) {
            transfer(target);
        }
    }

}
