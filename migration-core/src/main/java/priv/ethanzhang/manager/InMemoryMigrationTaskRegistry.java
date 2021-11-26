package priv.ethanzhang.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import priv.ethanzhang.config.GlobalConfig;
import priv.ethanzhang.event.MigrationTaskEvictedEvent;
import priv.ethanzhang.task.MigrationTask;

import java.time.Duration;
import java.util.Map;

class InMemoryMigrationTaskRegistry implements MigrationTaskRegistry {

    private final Cache<String, MigrationTask<?, ?>> migrationTasks;

    {
        migrationTasks = Caffeine.newBuilder()
                .initialCapacity(GlobalConfig.INSTANCE.getLocalMigrationTaskManagerConfig().getInitialCapacity())
                .maximumSize(GlobalConfig.INSTANCE.getLocalMigrationTaskManagerConfig().getMaximumSize())
                .expireAfterWrite(Duration.ofSeconds(GlobalConfig.INSTANCE.getLocalMigrationTaskManagerConfig().getExpireSeconds()))
                .evictionListener((taskId, task, cause) -> LocalMigrationTaskManager.INSTANCE.publishEvent(new MigrationTaskEvictedEvent((MigrationTask<?, ?>) task, cause)))
                .build();
    }

    @Override
    public void register(MigrationTask<?, ?> task) {

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
