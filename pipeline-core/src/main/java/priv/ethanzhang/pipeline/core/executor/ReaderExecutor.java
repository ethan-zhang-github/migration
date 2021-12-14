package priv.ethanzhang.pipeline.core.executor;

import priv.ethanzhang.pipeline.core.reader.PipeReader;
import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 读取执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
public interface ReaderExecutor<I, O> {

    /**
     * 执行
     */
    void start(PipeTask<I, O> task, PipeReader<I> reader);

    /**
     * 暂停
     */
    void stop(PipeTask<I, O> task, PipeReader<I> reader);

    /**
     * 终止
     */
    void shutDown(PipeTask<I, O> task, PipeReader<I> reader);

}
