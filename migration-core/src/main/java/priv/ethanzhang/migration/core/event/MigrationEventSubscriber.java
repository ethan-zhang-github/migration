package priv.ethanzhang.migration.core.event;

/**
 * 事件订阅者
 * @param <E> 事件类型
 */
@FunctionalInterface
public interface MigrationEventSubscriber<E extends MigrationEvent> {

    void subscribe(E event);

    default void handleException(E event, Throwable throwable) {}

}
