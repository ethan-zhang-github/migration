package com.aihuishou.pipeline.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

public class RedissonConfig {

    public static RedissonClient getRedissonClient() {
        return Redisson.create();
    }

}
