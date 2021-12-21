package com.aihuishou.pipeline.core.common;

import java.util.concurrent.atomic.AtomicReference;

public class LocalOnceHolder<T> implements Holder<T> {

    private final AtomicReference<T> holder = new AtomicReference<>();

    @Override
    public void set(T target) {
        holder.compareAndSet(null, target);
    }

    @Override
    public T get() {
        return holder.get();
    }

    @Override
    public void reset() {
        holder.set(null);
    }

    @Override
    public boolean isPresent() {
        return holder.get() != null;
    }

}
