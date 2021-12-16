package com.aihuishou.core.event.dispatcher;

import com.aihuishou.core.event.TaskEvent;
import com.aihuishou.core.event.TaskLifecycleEvent;
import com.aihuishou.core.event.subscriber.TaskEventSubscriber;
import com.aihuishou.core.task.PipeTask;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 本地事件分发器（基于 guava event bus 实现）
 * @author ethan zhang
 */
public enum GuavaTaskEventDispatcher implements TaskEventDispatcher {

    INSTANCE;

    private final EventBus eventBus = new AsyncEventBus(GuavaTaskEventDispatcher.class.getName(), Executors.newFixedThreadPool(1));

    private final ConcurrentMap<String, ConcurrentLinkedDeque<TaskLifecycleEvent>> eventStream = new ConcurrentHashMap<>();

    private final ConcurrentLinkedQueue<TaskEventSubscriber> subscribers = new ConcurrentLinkedQueue<>();

    {
        eventBus.register(new Subscriber());
    }

    @Override
    public void dispatch(TaskEvent event) {
        eventBus.post(event);
        if (event instanceof TaskLifecycleEvent) {
            TaskLifecycleEvent taskEvent = (TaskLifecycleEvent) event;
            PipeTask<?, ?> task = taskEvent.getTask();
            eventStream.putIfAbsent(task.getTaskId(), new ConcurrentLinkedDeque<>());
            eventStream.get(task.getTaskId()).add(taskEvent);
        }
    }

    @Override
    public void addSubsriber(TaskEventSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public List<TaskLifecycleEvent> getTaskEventStream(String taskId) {
        return new ArrayList<>(eventStream.get(taskId));
    }

    @Override
    public void clearTaskEventStream(String taskId) {
        eventStream.remove(taskId);
    }

    private class Subscriber {

        @SuppressWarnings("unused")
        @Subscribe
        public void subscribeMigrationEvent(TaskEvent event) {
            for (TaskEventSubscriber subscriber : subscribers) {
                if (subscriber.supports(event)) {
                    try {
                        subscriber.subscribe(event);
                    } catch (Exception e) {
                        try {
                            subscriber.handleException(event, e);
                        } catch (Exception ee) {
                            // do nothing
                        }
                    }
                }
            }
        }

    }

}
