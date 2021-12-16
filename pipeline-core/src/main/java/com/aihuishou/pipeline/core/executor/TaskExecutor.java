package com.aihuishou.pipeline.core.executor;

import com.aihuishou.pipeline.core.task.PipeTask;

/**
 * 任务执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
public interface TaskExecutor<I, O> {

    /**
     * 执行任务
     */
    void start(PipeTask<I, O> task);

    /**
     * 暂停任务
     */
    void stop(PipeTask<I, O> task);

    /**
     * 终止任务
     */
    void shutDown(PipeTask<I, O> task);

}
