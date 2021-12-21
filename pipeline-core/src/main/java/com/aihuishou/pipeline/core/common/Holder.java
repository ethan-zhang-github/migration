package com.aihuishou.pipeline.core.common;

public interface Holder<T> {

    void set(T target);

    T get();

    void reset();

    boolean isPresent();

    default void set(Holder<T> holder) {
        set(holder.get());
    }

}
