package com.aihuishou.pipeline.redisson.common;

import com.aihuishou.pipeline.core.common.Marker;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;

import java.util.UUID;

public class RedissonMarker implements Marker {

    private final RBitSet bitSet;

    public RedissonMarker(RedissonClient redissonClient) {
        this.bitSet = redissonClient.getBitSet(RedissonKey.REDISSON_MARKER + UUID.randomUUID());
    }

    @Override
    public boolean mark() {
        return bitSet.set(0);
    }

    @Override
    public boolean isMarked() {
        return bitSet.get(0);
    }

    @Override
    public void reset() {
        bitSet.set(0, false);
    }

}
