package priv.ethanzhang.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import priv.ethanzhang.config.GlobalConfig;
import priv.ethanzhang.event.MigrationTaskEvictedEvent;
import priv.ethanzhang.task.MigrationTask;

import java.time.Duration;
import java.util.Map;

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
                .evictionListener((taskId, task, cause) -> LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskEvictedEvent((MigrationTask<?, ?>) task, cause)))
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
