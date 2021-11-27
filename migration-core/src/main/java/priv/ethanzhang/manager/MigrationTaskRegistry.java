package priv.ethanzhang.manager;

import priv.ethanzhang.task.MigrationTask;

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
