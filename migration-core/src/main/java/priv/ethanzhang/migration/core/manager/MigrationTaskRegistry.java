package priv.ethanzhang.migration.core.manager;

import priv.ethanzhang.migration.core.task.MigrationTask;

import java.util.Map;

/**
 * 任务注册表
 */
public interface MigrationTaskRegistry {

    void register(MigrationTask<?, ?> task);

    void unregister(MigrationTask<?, ?> task);

    Map<String, MigrationTask<?, ?>> getAll();

    void clear();

}
