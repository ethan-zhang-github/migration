package com.aihuishou.pipeline.core.common;

import java.util.concurrent.atomic.LongAdder;

public class LocalCounter implements Counter {

    private final LongAdder longAdder = new LongAdder();

    @Override
    public void incr() {
        longAdder.increment();
    }

    @Override
    public void incr(long delta) {
        longAdder.add(delta);
    }

    @Override
    public void decr() {
        longAdder.decrement();
    }

    @Override
    public void decr(long delta) {
        longAdder.add(-delta);
    }

    @Override
    public long get() {
        return longAdder.longValue();
    }

    @Override
    public void reset() {
        longAdder.reset();
    }

    @Override
    public String toString() {
        return longAdder.toString();
    }

}
