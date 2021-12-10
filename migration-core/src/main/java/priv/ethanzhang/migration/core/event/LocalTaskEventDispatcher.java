package priv.ethanzhang.migration.core.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import priv.ethanzhang.migration.core.task.MigrationTask;
import priv.ethanzhang.migration.core.utils.GenericUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 本地事件分发器
 */
public class LocalTaskEventDispatcher implements TaskEventDispatcher {

    public static final LocalTaskEventDispatcher INSTANCE = new LocalTaskEventDispatcher();

    private final EventBus eventBus = new AsyncEventBus(LocalTaskEventDispatcher.class.getName(), Executors.newFixedThreadPool(1));

    private final ConcurrentMap<String, ConcurrentLinkedDeque<TaskTaskLifecycleEvent>> eventStream = new ConcurrentHashMap<>();

    private final List<TaskEventSubscriber<?>> subscribers = new CopyOnWriteArrayList<>();

    private LocalTaskEventDispatcher() {
        eventBus.register(new Subscriber());
    }

    @Override
    public void dispatch(TaskEvent event) {
        eventBus.post(event);
        if (event instanceof TaskTaskLifecycleEvent) {
            TaskTaskLifecycleEvent taskEvent = (TaskTaskLifecycleEvent) event;
            MigrationTask<?, ?> task = taskEvent.getTask();
            if (task != null) {
                eventStream.putIfAbsent(task.getTaskId(), new ConcurrentLinkedDeque<>());
                eventStream.get(task.getTaskId()).add(taskEvent);
            }
        }
    }

    @Override
    public void addSubsriber(TaskEventSubscriber<?> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public List<TaskTaskLifecycleEvent> getEventStream(String taskId) {
        return new ArrayList<>(eventStream.get(taskId));
    }

    @Override
    public void clearEventStream(String taskId) {
        eventStream.remove(taskId);
    }

    private class Subscriber {

        @SuppressWarnings({"all"})
        @Subscribe
        public void subscribeMigrationEvent(TaskEvent event) {
            for (TaskEventSubscriber subscriber : subscribers) {
                Class<?> eventType = GenericUtil.getInterfaceGenericType(subscriber.getClass(), TaskEventSubscriber.class, 0);
                if (eventType.isAssignableFrom(event.getClass())) {
                    try {
                        subscriber.subscribe(event);
                    } catch (Exception e) {
                        try {
                            subscriber.handleException(event, e);
                        } catch (Exception ee) {}
                    }
                }
            }
        }

    }

}
