package com.aihuishou.pipeline.core.manager;

import com.aihuishou.pipeline.core.config.GlobalConfig;
import com.aihuishou.pipeline.core.event.TaskEvictedEvent;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;

import java.util.Map;
import java.util.Optional;

/**
 * 任务注册表（基于内存的实现）
 * @author ethan zhang
 */
class InMemoryTaskRegistry implements TaskRegistry {

    private final Cache<String, PipeTask<?, ?>> cache;

    {
        cache = Caffeine.newBuilder()
                .initialCapacity(GlobalConfig.LOCAL_REGISTRY.getInitialCapacity())
                .maximumSize(GlobalConfig.LOCAL_REGISTRY.getMaximumSize())
                .expireAfterWrite(GlobalConfig.LOCAL_REGISTRY.getTimeout())
                .evictionListener((RemovalListener<String, PipeTask<?, ?>>) (taskId, task, cause) ->
                        Optional.ofNullable(task).ifPresent(t -> t.getDispatcher().dispatch(new TaskEvictedEvent(t, cause))))
                .build();
    }

    @Override
    public void register(PipeTask<?, ?> task) {
        cache.put(task.getTaskId(), task);
    }

    @Override
    public void unregister(PipeTask<?, ?> task) {
        cache.invalidate(task.getTaskId());
    }

    @Override
    public Map<String, PipeTask<?, ?>> getAll() {
        return cache.asMap();
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

}
