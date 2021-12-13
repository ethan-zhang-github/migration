package priv.ethanzhang.pipeline.core.event.dispatcher;

import priv.ethanzhang.pipeline.core.event.TaskEvent;
import priv.ethanzhang.pipeline.core.event.TaskLifecycleEvent;
import priv.ethanzhang.pipeline.core.event.subscriber.TaskEventSubscriber;

import java.util.List;

public class DisruptorTaskEventDispatcher implements TaskEventDispatcher {

    @Override
    public void dispatch(TaskEvent event) {

    }

    @Override
    public void addSubsriber(TaskEventSubscriber subscriber) {

    }

    @Override
    public List<TaskLifecycleEvent> getTaskEventStream(String taskId) {
        return null;
    }

    @Override
    public void clearTaskEventStream(String taskId) {

    }

}
