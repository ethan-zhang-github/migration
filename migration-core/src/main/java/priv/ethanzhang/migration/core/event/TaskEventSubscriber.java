package priv.ethanzhang.migration.core.event;

/**
 * 事件订阅者
 * @param <E> 事件类型
 */
@FunctionalInterface
public interface TaskEventSubscriber<E extends TaskEvent> {

    void subscribe(E event);

    default void handleException(E event, Throwable throwable) {}

}
