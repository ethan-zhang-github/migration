package priv.ethanzhang.executor;

import priv.ethanzhang.task.MigrationTask;

/**
 * 任务执行器
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
public interface MigrationTaskExecutor<I, O> {

    /**
     * 执行任务
     */
    void execute(MigrationTask<I, O> task);

    /**
     * 暂停任务
     */
    void stop(MigrationTask<I, O> task);

    /**
     * 终止任务
     */
    void shutDown(MigrationTask<I, O> task);

}
