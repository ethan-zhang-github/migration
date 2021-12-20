package com.aihuishou.pipeline.core.executor;

import com.aihuishou.pipeline.core.task.PipeTask;
import com.aihuishou.pipeline.core.writer.PipeWriter;

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

    /**
     * 同步返回
     */
    void join(PipeTask<I, O> task, PipeWriter<O> writer);

}
