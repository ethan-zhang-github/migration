package priv.ethanzhang.executor;

import priv.ethanzhang.task.MigrationTask;

public interface MigrationTaskExecutor {

    <I, O> void execute(MigrationTask<I, O> task);

}
