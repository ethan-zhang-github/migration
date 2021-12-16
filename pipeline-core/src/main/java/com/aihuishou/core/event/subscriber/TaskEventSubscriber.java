package com.aihuishou.core.event.subscriber;

import com.aihuishou.core.event.TaskEvent;

/**
 * 任务事件订阅者
 * @author ethanzhang
 */
public interface TaskEventSubscriber {

    void subscribe(TaskEvent event);

    default boolean supports(TaskEvent event) {
        return true;
    }

    default void handleException(TaskEvent event, Throwable throwable) {}

}
