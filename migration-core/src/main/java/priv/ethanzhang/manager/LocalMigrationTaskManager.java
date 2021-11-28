package priv.ethanzhang.manager;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import priv.ethanzhang.event.MigrationTaskShutdownEvent;
import priv.ethanzhang.event.MigrationTaskStartedEvent;
import priv.ethanzhang.task.MigrationTask;

import java.util.Map;

/**
 * 本地任务管理器
 */
@Slf4j
public class LocalMigrationTaskManager implements MigrationTaskManager {

    public static final LocalMigrationTaskManager INSTANCE = new LocalMigrationTaskManager();

    private EventBus bus;

    private MigrationTaskRegistry registry;

    private LocalMigrationTaskManager() {
        initialize();
    }

    @Override
    public void initialize() {
        registry = new InMemoryMigrationTaskRegistry();
        bus = new EventBus(LocalMigrationTaskManager.class.getName());
        bus.register(new Subscriber(registry));
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
    }

    @Override
    public void shutDown() {
        Map<String, MigrationTask<?, ?>> migrationTaskMap = registry.getAll();
        migrationTaskMap.forEach(((taskId, task) -> {
            task.shutDown();
            log.warn("task [{}] has been shut down because local migration task manager has been shut down!", taskId);
        }));
        registry.clear();
    }

    @Override
    public void publishEvent(Object event) {
        bus.post(event);
    }

    private static class Subscriber {

        private final MigrationTaskRegistry registry;

        private Subscriber(MigrationTaskRegistry registry) {
            this.registry = registry;
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void subscribeMigrationTaskStartedEvent(MigrationTaskStartedEvent event) {
            registry.register(event.getTask());
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void subscribeMigrationTaskShutdownEvent(MigrationTaskShutdownEvent event) {
            registry.unregister(event.getTask());
        }

    }

}
