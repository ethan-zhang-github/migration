package com.aihuishou.pipeline.core.context;

import com.aihuishou.pipeline.core.common.CasHolder;
import com.aihuishou.pipeline.core.exception.StateTransferException;

import java.util.concurrent.atomic.AtomicReference;

public class LocalTaskStateHolder extends CasHolder<TaskState> implements TaskStateHolder {

    private final AtomicReference<TaskState> state;

    public LocalTaskStateHolder() {
        super(TaskState.NEW);
        this.state = new AtomicReference<>(initialVal);
    }

    @Override
    protected void validate(TaskState origin, TaskState target) {
        if (!TaskState.canTransfer(origin, target)) {
            throw new StateTransferException(origin, target);
        }
    }

    @Override
    protected boolean compareAndSet(TaskState origin, TaskState target) {
        return state.compareAndSet(origin, target);
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
    public String toString() {
        return state.toString();
    }

}
