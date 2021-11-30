package priv.ethanzhang.migration.core.event;

import java.util.List;

/**
 * 事件分发器
 */
public interface MigrationEventDispatcher {

    /**
     * 发布事件
     */
    void dispatch(MigrationEvent event);

    /**
     * 添加事件订阅者
     */
    void addSubsriber(MigrationEventSubscriber<?> subscriber);

    /**
     * 获取事件流
     */
    List<MigrationTaskLifecycleEvent> getEventStream(String taskId);

    /**
     * 清除事件流
     */
    void clearEventStream(String taskId);

}
