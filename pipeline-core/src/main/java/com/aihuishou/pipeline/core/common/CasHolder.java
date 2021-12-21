package com.aihuishou.pipeline.core.common;

import java.util.Objects;

public abstract class CasHolder<T> implements Holder<T> {

    protected T initialVal;

    public CasHolder(T initialVal) {
        this.initialVal = initialVal;
    }

    public CasHolder() {}

    @Override
    public void set(T target) {
        Objects.requireNonNull(target);
        T origin = get();
        if (origin == target) {
            return;
        }
        validate(origin, target);
        if (!compareAndSet(origin, target)) {
            set(target);
        }
    }

    protected abstract void validate(T origin, T target);

    protected abstract boolean compareAndSet(T origin, T target);

}
