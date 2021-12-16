package com.aihuishou.core.event.dispatcher;

import com.aihuishou.core.event.TaskEvent;
import com.aihuishou.core.event.TaskLifecycleEvent;
import com.aihuishou.core.event.subscriber.TaskEventSubscriber;

import java.util.List;

/**
 * 任务事件分发器
 * @author ethan zhang
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
