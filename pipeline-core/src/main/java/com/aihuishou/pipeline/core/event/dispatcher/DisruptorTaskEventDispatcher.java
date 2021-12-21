package com.aihuishou.pipeline.core.event.dispatcher;

import com.aihuishou.pipeline.core.common.LocalHolder;
import com.aihuishou.pipeline.core.config.GlobalConfig;
import com.aihuishou.pipeline.core.event.TaskEvent;
import com.aihuishou.pipeline.core.event.TaskLifecycleEvent;
import com.aihuishou.pipeline.core.event.subscriber.TaskEventSubscriber;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 本地事件分发器（基于 disruptor 实现）
 * @author ethan zhang
 */
public enum DisruptorTaskEventDispatcher implements TaskEventDispatcher {

    INSTANCE;

    private final ConcurrentLinkedQueue<TaskEventSubscriber> subscribers;

    private final Disruptor<LocalHolder<TaskEvent>> disruptor;

    private final Cache<String, ConcurrentLinkedQueue<TaskLifecycleEvent>> eventStream;

    {
        subscribers = new ConcurrentLinkedQueue<>();
        eventStream = Caffeine.newBuilder()
                .initialCapacity(GlobalConfig.LOCAL_REGISTRY.getInitialCapacity())
                .maximumSize(GlobalConfig.LOCAL_REGISTRY.getMaximumSize())
                .expireAfterWrite(GlobalConfig.LOCAL_REGISTRY.getTimeout())
                .build();
        disruptor = new Disruptor<>(LocalHolder::new, GlobalConfig.LOCAL_DISPATCHER.getBufferSize(), DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(this::onEvent);
        disruptor.start();
    }

    @Override
    public void dispatch(TaskEvent event) {
        disruptor.getRingBuffer().publishEvent(this::translate, event);
        if (event instanceof TaskLifecycleEvent) {
            PipeTask<?, ?> task = ((TaskLifecycleEvent) event).getTask();
            ConcurrentLinkedQueue<TaskLifecycleEvent> taskEventStream = eventStream.getIfPresent(task.getTaskId());
            if (CollectionUtils.isNotEmpty(taskEventStream)) {
                taskEventStream.add((TaskLifecycleEvent) event);
            } else {
                ConcurrentLinkedQueue<TaskLifecycleEvent> newTaskEventStream = new ConcurrentLinkedQueue<>();
                newTaskEventStream.add((TaskLifecycleEvent) event);
                eventStream.put(task.getTaskId(), newTaskEventStream);
            }
        }

    }

    @Override
    public void addSubsriber(TaskEventSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public List<TaskLifecycleEvent> getTaskEventStream(String taskId) {
        ConcurrentLinkedQueue<TaskLifecycleEvent> taskEventStream = eventStream.getIfPresent(taskId);
        if (CollectionUtils.isNotEmpty(taskEventStream)) {
            return new ArrayList<>(taskEventStream);
        }
        return Collections.emptyList();
    }

    @Override
    public void clearTaskEventStream(String taskId) {
        eventStream.invalidate(taskId);
    }

    private void translate(LocalHolder<TaskEvent> event, long sequence, TaskEvent data) {
        event.set(data);
    }

    private void onEvent(LocalHolder<TaskEvent> event, long sequence, boolean endOfBatch) {
        subscribers.stream().filter(subscriber -> subscriber.supports(event.get()))
                .forEach(subscriber -> {
                    try {
                        subscriber.subscribe(event.get());
                    } catch (Exception e) {
                        try {
                            subscriber.handleException(event.get(), e);
                        } catch (Exception he) {
                            // do nothing
                        }
                    }
                });
    }

}
