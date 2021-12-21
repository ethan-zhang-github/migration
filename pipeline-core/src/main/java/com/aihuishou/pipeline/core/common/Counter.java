package com.aihuishou.pipeline.core.common;

public interface Counter {

    void incr();

    void incr(long delta);

    void decr();

    void decr(long delta);

    long get();

    void reset();

}
