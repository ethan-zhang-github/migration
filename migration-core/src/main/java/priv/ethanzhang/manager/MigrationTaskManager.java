package priv.ethanzhang.manager;

public interface MigrationTaskManager {

    void initialize();

    void shutDown();

    void publishEvent(Object event);

}
