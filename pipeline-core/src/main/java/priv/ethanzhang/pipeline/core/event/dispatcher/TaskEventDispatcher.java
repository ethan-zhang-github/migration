package priv.ethanzhang.pipeline.core.event.dispatcher;

import priv.ethanzhang.pipeline.core.event.TaskEvent;
import priv.ethanzhang.pipeline.core.event.TaskLifecycleEvent;
import priv.ethanzhang.pipeline.core.event.subscriber.TaskEventSubscriber;

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
    void addSubsriber(TaskEventSubscriber subscriber);

    /**
     * 获取任务事件流
     */
    List<TaskLifecycleEvent> getTaskEventStream(String taskId);

    /**
     * 清除任务事件流
     */
    void clearTaskEventStream(String taskId);

}
