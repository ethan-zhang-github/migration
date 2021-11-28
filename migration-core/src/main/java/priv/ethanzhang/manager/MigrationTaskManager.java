package priv.ethanzhang.manager;

/**
 * 任务管理器
 */
public interface MigrationTaskManager {

    void initialize();

    void shutDown();

    void publishEvent(Object event);

}
