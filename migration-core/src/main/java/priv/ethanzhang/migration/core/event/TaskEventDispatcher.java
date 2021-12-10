package priv.ethanzhang.migration.core.event;

import java.util.List;

/**
 * 事件分发器
 */
public interface TaskEventDispatcher {

    /**
     * 发布事件
     */
    void dispatch(TaskEvent event);

    /**
     * 添加事件订阅者
     */
    void addSubsriber(TaskEventSubscriber<?> subscriber);

    /**
     * 获取事件流
     */
    List<TaskTaskLifecycleEvent> getEventStream(String taskId);

    /**
     * 清除事件流
     */
    void clearEventStream(String taskId);

}
