package priv.ethanzhang.task;

public interface MigrationTaskListener {

    default void beforeTaskExecute(MigrationTask<?, ?> task) {}

    default void afterTaskExecute(MigrationTask<?, ?> task) {}

    default void beforeTaskStop(MigrationTask<?, ?> task) {}

    default void afterTaskStop(MigrationTask<?, ?> task) {}

    default void beforeTaskShutdown(MigrationTask<?, ?> task) {}

    default void afterTaskShutdown(MigrationTask<?, ?> task) {}

    default void onTerminated(MigrationTask<?, ?> task) {}

    default void onFailed(MigrationTask<?, ?> task) {}

}
