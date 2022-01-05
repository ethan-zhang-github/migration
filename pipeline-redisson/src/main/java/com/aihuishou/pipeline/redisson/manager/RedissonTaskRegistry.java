package com.aihuishou.pipeline.redisson.manager;

import com.aihuishou.pipeline.core.manager.TaskRegistry;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.Map;

class RedissonTaskRegistry implements TaskRegistry {

    private final RMapCache<String, PipeTask<?, ?>> cache;

    public RedissonTaskRegistry(RedissonClient redissonClient) {
        cache = redissonClient.getMapCache(RedissonKey.REDISSON_TASK_REGISTRY + redissonClient.getId());
    }

    @Override
    public void register(PipeTask<?, ?> task) {
        cache.put(task.getTaskId(), task);
    }

    @Override
    public void unregister(PipeTask<?, ?> task) {

    }

    @Override
    public Map<String, PipeTask<?, ?>> getAll() {
        return null;
    }

    @Override
    public void clear() {

    }

}
