package com.aihuishou.pipeline.redisson.common;

import com.aihuishou.pipeline.core.common.Holder;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedissonHolder<T> implements Holder<T> {

    private final RBucket<T> bucket;

    private final Duration ttl;

    private T initialVal;

    public RedissonHolder(RedissonClient redissonClient, Duration ttl) {
        this.bucket = redissonClient.getBucket(RedissonKey.REDISSON_HOLDER + UUID.randomUUID());
        this.ttl = ttl;
    }

    public RedissonHolder(RedissonClient redissonClient, Duration ttl, T initialVal) {
        this.bucket = redissonClient.getBucket(RedissonKey.REDISSON_HOLDER + UUID.randomUUID());
        this.ttl = ttl;
        this.initialVal = initialVal;
        bucket.set(initialVal);
    }

    @Override
    public void set(T target) {
        bucket.set(target, ttl.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public T get() {
        return bucket.get();
    }

    @Override
    public void reset() {
        bucket.set(initialVal);
    }

    @Override
    public boolean isPresent() {
        return bucket.isExists();
    }

}
