package com.aihuishou.pipeline.core.manager;

public abstract class AbstractTaskScheduler {

    protected TaskRegistry taskRegistry;

    public AbstractTaskScheduler(TaskRegistry taskRegistry) {
        this.taskRegistry = taskRegistry;
    }

    protected abstract void start();

    protected abstract void shutdown();

}
