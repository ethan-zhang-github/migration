package com.aihuishou.pipeline.redisson.common;

import com.aihuishou.pipeline.core.common.Counter;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RLongAdder;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedissonCounter implements Counter {

    private final RLongAdder longAdder;

    public RedissonCounter(RedissonClient redissonClient, Duration ttl, String id) {
        this.longAdder = redissonClient.getLongAdder(RedissonKey.REDISSON_COUNTER + id);
        longAdder.expire(ttl.getSeconds(), TimeUnit.SECONDS);
    }

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
        return longAdder.sum();
    }

    @Override
    public void reset() {
        longAdder.reset();
    }

}
