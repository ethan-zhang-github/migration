package com.aihuishou.pipeline.redisson.common;

import com.aihuishou.pipeline.core.common.Holder;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedissonOnceHolder<T> implements Holder<T> {

    private final RBucket<T> bucket;

    private final Duration ttl;

    public RedissonOnceHolder(RedissonClient redissonClient, Duration ttl) {
        this.bucket = redissonClient.getBucket(RedissonKey.REDISSON_HOLDER + UUID.randomUUID());
        this.ttl = ttl;
    }

    @Override
    public void set(T target) {
        bucket.compareAndSet(null, target);
        bucket.expire(ttl.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public T get() {
        return bucket.get();
    }

    @Override
    public void reset() {
        bucket.set(null);
    }

    @Override
    public boolean isPresent() {
        return bucket.isExists();
    }

}
