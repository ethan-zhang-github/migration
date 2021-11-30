package priv.ethanzhang.migration.core.manager;

import lombok.extern.slf4j.Slf4j;
import priv.ethanzhang.migration.core.event.*;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.util.Map;

/**
 * 本地任务管理器
 */
@Slf4j
public class LocalMigrationTaskManager implements MigrationTaskManager {

    public static final LocalMigrationTaskManager INSTANCE = new LocalMigrationTaskManager();

    private MigrationTaskRegistry registry;

    private LocalMigrationTaskManager() {
        initialize();
    }

    @Override
    public void initialize() {
        registry = new InMemoryMigrationTaskRegistry();
        LocalMigrationEventDispatcher.INSTANCE.addSubsriber((MigrationEventSubscriber<MigrationTaskLifecycleEvent>) event -> {
            MigrationTask<?, ?> task = event.getTask();
            if (event instanceof MigrationTaskStartedEvent) {
                registry.register(task);
            }
            if (event instanceof MigrationTaskShutdownEvent) {
                task.getDispatcher().clearEventStream(task.getTaskId());
                registry.unregister(task);
            }
            if (event instanceof MigrationTaskFinishedEvent) {
                task.getDispatcher().clearEventStream(task.getTaskId());
                registry.unregister(task);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
    }

    @Override
    public void shutDown() {
        Map<String, MigrationTask<?, ?>> migrationTaskMap = registry.getAll();
        migrationTaskMap.forEach(((taskId, task) -> {
            task.shutDown();
            task.getDispatcher().clearEventStream(taskId);
            log.warn("task [{}] has been shut down because local migration task manager has been shut down!", taskId);
        }));
        registry.clear();
    }

}
