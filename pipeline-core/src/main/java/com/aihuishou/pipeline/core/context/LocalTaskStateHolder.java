package com.aihuishou.pipeline.core.context;

import com.aihuishou.pipeline.core.common.CasHolder;
import com.aihuishou.pipeline.core.exception.StateTransferException;

import java.util.concurrent.atomic.AtomicReference;

public class LocalTaskStateHolder extends CasHolder<TaskState> {

    private final AtomicReference<TaskState> state;

    public LocalTaskStateHolder() {
        super(TaskState.NEW);
        this.state = new AtomicReference<>(initialVal);
    }

    @Override
    public TaskState get() {
        return state.get();
    }

    @Override
    public void reset() {
        state.set(initialVal);
    }

    @Override
    public boolean isPresent() {
        return state.get() != null;
    }

    @Override
    protected void validate(TaskState origin, TaskState target) {
        switch (target) {
            case NEW:
                throw new StateTransferException(origin, target);
            case RUNNING:
                if (!origin.canRun()) {
                    throw new StateTransferException(origin, target);
                }
                break;
            case STOPPING:
                if (!origin.canStop()) {
                    throw new StateTransferException(origin, target);
                }
                break;
            case TERMINATED:
                if (origin != TaskState.RUNNING && origin != TaskState.STOPPING) {
                    throw new StateTransferException(origin, target);
                }
                break;
            case FAILED:
                if (origin != TaskState.RUNNING) {
                    throw new StateTransferException(origin, target);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean compareAndSet(TaskState origin, TaskState target) {
        return state.compareAndSet(origin, target);
    }

    @Override
    public String toString() {
        return state.toString();
    }

}
