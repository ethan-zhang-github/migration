package com.aihuishou.pipeline.redisson.common;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

public class RedissonBootstrap {

    public static RedissonClient getRedissonClient() {
        return Redisson.create();
    }

}
