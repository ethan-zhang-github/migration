package com.aihuishou.pipeline.redisson.context;

import com.aihuishou.pipeline.core.common.CasHolder;
import com.aihuishou.pipeline.core.context.TaskState;
import com.aihuishou.pipeline.core.exception.StateTransferException;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.UUID;

public class RedissonTaskStateHolder extends CasHolder<TaskState> {

    protected RBucket<TaskState> state;

    public RedissonTaskStateHolder(RedissonClient redissonClient) {
        this.state = redissonClient.getBucket(RedissonKey.REDISSON_TASK_STATE + UUID.randomUUID());
    }

    @Override
    protected void validate(TaskState origin, TaskState target) {
        if (!TaskState.canTransfer(origin, target)) {
            throw new StateTransferException(origin, target);
        }
    }

    @Override
    protected boolean compareAndSet(TaskState origin, TaskState target) {
        return state.compareAndSet(origin, target);
    }

    @Override
    public TaskState get() {
        return state.get();
    }

    @Override
    public void reset() {
        state.set(initialVal);
    }

    @Override
    public boolean isPresent() {
        return state.isExists();
    }

}
