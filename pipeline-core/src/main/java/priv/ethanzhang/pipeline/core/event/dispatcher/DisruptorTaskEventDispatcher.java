package priv.ethanzhang.pipeline.core.event.dispatcher;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.pipeline.core.config.GlobalConfig;
import priv.ethanzhang.pipeline.core.event.TaskEvent;
import priv.ethanzhang.pipeline.core.event.TaskLifecycleEvent;
import priv.ethanzhang.pipeline.core.event.subscriber.TaskEventSubscriber;
import priv.ethanzhang.pipeline.core.model.Wrapper;
import priv.ethanzhang.pipeline.core.task.PipeTask;

import java.time.Duration;
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

    private final Disruptor<Wrapper<TaskEvent>> disruptor;

    private final Cache<String, ConcurrentLinkedQueue<TaskLifecycleEvent>> eventStream;

    {
        subscribers = new ConcurrentLinkedQueue<>();
        eventStream = Caffeine.newBuilder()
                .initialCapacity(GlobalConfig.LOCAL_REGISTRY.getInitialCapacity())
                .maximumSize(GlobalConfig.LOCAL_REGISTRY.getMaximumSize())
                .expireAfterWrite(Duration.ofSeconds(GlobalConfig.LOCAL_REGISTRY.getExpireSeconds()))
                .build();
        disruptor = new Disruptor<>(Wrapper::new, GlobalConfig.LOCAL_DISPATCHER.getBufferSize(), DaemonThreadFactory.INSTANCE);
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

    private void translate(Wrapper<TaskEvent> event, long sequence, TaskEvent data) {
        event.setData(data);
    }

    private void onEvent(Wrapper<TaskEvent> event, long sequence, boolean endOfBatch) {
        subscribers.stream().filter(subscriber -> subscriber.supports(event.getData()))
                .forEach(subscriber -> {
                    try {
                        subscriber.subscribe(event.getData());
                    } catch (Exception e) {
                        try {
                            subscriber.handleException(event.getData(), e);
                        } catch (Exception he) {
                            // do nothing
                        }
                    }
                });
    }

}
