package com.aihuishou.core.executor;

import com.aihuishou.core.task.PipeTask;
import com.aihuishou.core.writer.PipeWriter;

/**
 * 写入执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
public interface WriterExecutor<I, O> {

    /**
     * 执行
     */
    void start(PipeTask<I, O> task, PipeWriter<O> writer);

    /**
     * 暂停
     */
    void stop(PipeTask<I, O> task, PipeWriter<O> writer);

    /**
     * 终止
     */
    void shutDown(PipeTask<I, O> task, PipeWriter<O> writer);

}
