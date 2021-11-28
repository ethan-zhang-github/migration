package priv.ethanzhang.executor;

import priv.ethanzhang.task.MigrationTask;

/**
 * 任务执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
public interface MigrationTaskExecutor<I, O> {

    void execute(MigrationTask<I, O> task);

    void stop(MigrationTask<I, O> task);

    void shutDown(MigrationTask<I, O> task);

}
