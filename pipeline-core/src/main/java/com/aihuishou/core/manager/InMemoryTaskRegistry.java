package com.aihuishou.core.manager;

import com.aihuishou.core.config.GlobalConfig;
import com.aihuishou.core.event.TaskEvictedEvent;
import com.aihuishou.core.task.PipeTask;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * 任务注册表（基于内存的实现）
 * @author ethan zhang
 */
class InMemoryTaskRegistry implements TaskRegistry {

    private final Cache<String, PipeTask<?, ?>> migrationTasks;

    {
        migrationTasks = Caffeine.newBuilder()
                .initialCapacity(GlobalConfig.LOCAL_REGISTRY.getInitialCapacity())
                .maximumSize(GlobalConfig.LOCAL_REGISTRY.getMaximumSize())
                .expireAfterWrite(Duration.ofSeconds(GlobalConfig.LOCAL_REGISTRY.getExpireSeconds()))
                .evictionListener((RemovalListener<String, PipeTask<?, ?>>) (taskId, task, cause) ->
                        Optional.ofNullable(task).ifPresent(t -> t.getDispatcher().dispatch(new TaskEvictedEvent(t, cause))))
                .build();
    }

    @Override
    public void register(PipeTask<?, ?> task) {
        migrationTasks.put(task.getTaskId(), task);
    }

    @Override
    public void unregister(PipeTask<?, ?> task) {
        migrationTasks.invalidate(task.getTaskId());
    }

    @Override
    public Map<String, PipeTask<?, ?>> getAll() {
        return migrationTasks.asMap();
    }

    @Override
    public void clear() {
        migrationTasks.invalidateAll();
    }

}
