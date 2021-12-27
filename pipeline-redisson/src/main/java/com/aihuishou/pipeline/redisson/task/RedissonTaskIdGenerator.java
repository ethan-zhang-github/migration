package com.aihuishou.pipeline.redisson.task;

import com.aihuishou.pipeline.core.task.TaskIdGenerator;
import com.aihuishou.pipeline.redisson.common.RedissonBootstrap;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;

import java.util.concurrent.atomic.AtomicReference;

public class RedissonTaskIdGenerator implements TaskIdGenerator {

    private static final String PREFIX = "REDISSON-TASK-";

    private static final AtomicReference<RedissonTaskIdGenerator> CACHE = new AtomicReference<>();

    private final RIdGenerator idGenerator;

    private RedissonTaskIdGenerator(RedissonClient redissonClient) {
        this.idGenerator = redissonClient.getIdGenerator(RedissonKey.REDISSON_TASK_ID_GENERATOR + redissonClient.getId());
        idGenerator.tryInit(System.currentTimeMillis(), Long.MAX_VALUE);
    }

    public static RedissonTaskIdGenerator getInstance() {
        if (CACHE.get() != null) {
            return CACHE.get();
        }
        CACHE.compareAndSet(null, new RedissonTaskIdGenerator(RedissonBootstrap.getRedissonClient()));
        return getInstance();
    }

    @Override
    public String generate() {
        return PREFIX + idGenerator.nextId();
    }

}
