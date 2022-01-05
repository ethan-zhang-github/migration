package com.aihuishou.pipeline.redisson.event.dispatcher;

import com.aihuishou.pipeline.core.event.TaskEvent;
import com.aihuishou.pipeline.core.event.TaskLifecycleEvent;
import com.aihuishou.pipeline.core.event.dispatcher.TaskEventDispatcher;
import com.aihuishou.pipeline.core.event.subscriber.TaskEventSubscriber;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.stream.StreamAddArgs;

import java.util.List;

public class RedissonTaskEventDispatcher implements TaskEventDispatcher {

    private final RStream<Long, Object> stream;

    public RedissonTaskEventDispatcher(RedissonClient redissonClient) {
        this.stream = redissonClient.getStream(RedissonKey.REDISSON_TASK_EVENT_DISPATCHER);
        stream.add(StreamAddArgs.entry(0L, "0"));
    }
    
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
