package priv.ethanzhang.manager;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import priv.ethanzhang.task.MigrationTask;

import java.util.Map;

/**
 * 本地任务管理器
 * @author ethan zhang
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
        bus.register(new Subscriber());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
    }

    @Override
    public void shutDown() {
        Map<String, MigrationTask<?, ?>> migrationTaskMap = registry.getAll();
        migrationTaskMap.forEach(((taskId, task) -> {
            task.stop();
            log.warn("task [{}] has been stopped because local migration task manager has been shut down!", taskId);
        }));
        registry.clear();
    }

    @Override
    public void publishEvent(Object event) {
        bus.post(event);
    }

    private static class Subscriber {



    }

}
