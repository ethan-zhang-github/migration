package priv.ethanzhang.migration.core.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import priv.ethanzhang.migration.core.task.MigrationTask;
import priv.ethanzhang.migration.core.utils.GenericUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;

/**
 * 本地事件分发器
 */
public class LocalMigrationEventDispatcher implements MigrationEventDispatcher {

    public static final LocalMigrationEventDispatcher INSTANCE = new LocalMigrationEventDispatcher();

    private final EventBus eventBus = new EventBus();

    private final ConcurrentMap<String, ConcurrentLinkedDeque<MigrationTaskLifecycleEvent>> eventStream = new ConcurrentHashMap<>();

    private final List<MigrationEventSubscriber<?>> subscribers = new ArrayList<>();

    private LocalMigrationEventDispatcher() {
        eventBus.register(new Subscriber());
    }

    @Override
    public void dispatch(MigrationEvent event) {
        eventBus.post(event);
        if (event instanceof MigrationTaskLifecycleEvent) {
            MigrationTaskLifecycleEvent taskEvent = (MigrationTaskLifecycleEvent) event;
            MigrationTask<?, ?> task = taskEvent.getTask();
            if (task != null) {
                eventStream.putIfAbsent(task.getTaskId(), new ConcurrentLinkedDeque<>());
                eventStream.get(task.getTaskId()).add(taskEvent);
            }
        }
    }

    @Override
    public void addSubsriber(MigrationEventSubscriber<?> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public List<MigrationTaskLifecycleEvent> getEventStream(String taskId) {
        return new ArrayList<>(eventStream.get(taskId));
    }

    @Override
    public void clearEventStream(String taskId) {
        eventStream.remove(taskId);
    }

    private class Subscriber {

        @SuppressWarnings({"all"})
        @Subscribe
        public void subscribeMigrationEvent(MigrationEvent event) {
            for (MigrationEventSubscriber subscriber : subscribers) {
                Class<?> eventType = GenericUtil.getInterfaceGenericType(subscriber.getClass(), MigrationEventSubscriber.class, 0);
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
