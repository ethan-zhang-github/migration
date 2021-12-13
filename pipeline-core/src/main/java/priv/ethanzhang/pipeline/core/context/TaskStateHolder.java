package priv.ethanzhang.pipeline.core.context;

import priv.ethanzhang.pipeline.core.exception.StateTransferException;

import java.util.concurrent.atomic.AtomicReference;

public class TaskStateHolder {

    private final AtomicReference<TaskState> state = new AtomicReference<>(TaskState.NEW);

    public TaskState get() {
        return state.get();
    }

    public void transfer(TaskState target) {
        TaskState origin = state.get();
        if (origin == target) {
            return;
        }
        switch (origin) {
            case NEW:
                if (target != TaskState.RUNNING) {
                    throw new StateTransferException(origin, target);
                }
                break;
            case RUNNING:
                if (target == TaskState.NEW) {
                    throw new StateTransferException(origin, target);
                }
                break;
            case STOPPING:
                if (target != TaskState.RUNNING && target != TaskState.TERMINATED) {
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
