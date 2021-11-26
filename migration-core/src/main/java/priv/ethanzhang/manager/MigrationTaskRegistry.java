package priv.ethanzhang.manager;

import priv.ethanzhang.task.MigrationTask;

import java.util.Map;

public interface MigrationTaskRegistry {

    void register(MigrationTask<?, ?> task);

    Map<String, MigrationTask<?, ?>> getAll();

    void clear();

}
