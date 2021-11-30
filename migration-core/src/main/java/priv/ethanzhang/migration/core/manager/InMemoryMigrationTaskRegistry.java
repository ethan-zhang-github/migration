package priv.ethanzhang.migration.core.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import priv.ethanzhang.migration.core.config.GlobalConfig;
import priv.ethanzhang.migration.core.event.MigrationTaskEvictedEvent;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * 任务注册表（基于内存的实现）
 */
class InMemoryMigrationTaskRegistry implements MigrationTaskRegistry {

    private final Cache<String, MigrationTask<?, ?>> migrationTasks;

    {
        migrationTasks = Caffeine.newBuilder()
                .initialCapacity(GlobalConfig.LOCAL_REGISTRY.getInitialCapacity())
                .maximumSize(GlobalConfig.LOCAL_REGISTRY.getMaximumSize())
                .expireAfterWrite(Duration.ofSeconds(GlobalConfig.LOCAL_REGISTRY.getExpireSeconds()))
                .evictionListener((RemovalListener<String, MigrationTask<?, ?>>) (taskId, task, cause) ->
                        Optional.ofNullable(task).ifPresent(t -> t.getDispatcher().dispatch(new MigrationTaskEvictedEvent(t, cause))))
                .build();
    }

    @Override
    public void register(MigrationTask<?, ?> task) {
        migrationTasks.put(task.getTaskId(), task);
    }

    @Override
    public void unregister(MigrationTask<?, ?> task) {
        migrationTasks.invalidate(task.getTaskId());
    }

    @Override
    public Map<String, MigrationTask<?, ?>> getAll() {
        return migrationTasks.asMap();
    }

    @Override
    public void clear() {
        migrationTasks.invalidateAll();
    }

}
