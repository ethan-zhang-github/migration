package priv.ethanzhang.migration.core.reporter;

import priv.ethanzhang.migration.core.task.MigrationTask;

/**
 * 任务状态报告
 */
public interface MigrationTaskReporter {

    void report(MigrationTask<?, ?> task);

}
