package com.aihuishou.pipeline.core.common;

import java.util.Objects;

public class LocalHolder<T> implements Holder<T> {

    private T initialVal;

    private T data;

    public LocalHolder(T initialVal) {
        this.initialVal = initialVal;
        this.data = initialVal;
    }

    public LocalHolder() {}

    @Override
    public void set(T target) {
        data = target;
    }

    @Override
    public T get() {
        return data;
    }

    @Override
    public void reset() {
        data = initialVal;
    }

    @Override
    public boolean isPresent() {
        return data != null;
    }

    @Override
    public String toString() {
        return Objects.toString(data);
    }

}
