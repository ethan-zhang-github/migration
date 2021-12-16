package com.aihuishou.pipeline.core.event.subscriber;

import com.aihuishou.pipeline.core.event.TaskEvent;
import com.aihuishou.pipeline.core.utils.GenericUtil;

/**
 * 任务事件订阅者（指定事件类型）
 * @param <E> 事件类型
 */
public abstract class GenericTaskEventSubscriber<E extends TaskEvent> implements TaskEventSubscriber {

    private final Class<E> eventType;

    public GenericTaskEventSubscriber(Class<E> eventType) {
        this.eventType = eventType;
    }

    public GenericTaskEventSubscriber() {
        this.eventType = GenericUtil.getSuperclassGenericType(this.getClass(), 0);
    }

    @Override
    public boolean supports(TaskEvent event) {
        return eventType.isAssignableFrom(event.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void subscribe(TaskEvent event) {
        subscribeInternal((E) event);
    }

    protected abstract void subscribeInternal(E event);

}
