package com.aihuishou.pipeline.core.executor;

import com.aihuishou.pipeline.core.task.PipeTask;
import com.aihuishou.pipeline.core.processor.PipeProcessorChain;

/**
 * processor 执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
public interface ProcessorExecutor<I, O> {

    /**
     * 开始
     */
    void start(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain);

    /**
     * 暂停
     */
    void stop(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain);

    /**
     * 终止
     */
    void shutDown(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain);

    /**
     * 同步返回
     */
    void join(PipeTask<I, O> task, PipeProcessorChain<I, O> processorChain);

}
