package com.aihuishou.pipeline.core.executor;

import lombok.Getter;

import java.util.concurrent.Executor;

/**
 * 本地任务执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
@Getter
public class LocalTaskExecutor<I, O> extends AbstractTaskExecutor<I, O> {

    public LocalTaskExecutor(Executor executor) {
        super(new LocalReaderExecutor<>(executor), new LocalProcessorExecutor<>(executor), new LocalWriterExecutor<>(executor));
    }

}
