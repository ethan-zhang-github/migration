package priv.ethanzhang.migration.core.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.event.MigrationEventDispatcher;
import priv.ethanzhang.migration.core.event.MigrationEventSubscriber;
import priv.ethanzhang.migration.core.executor.MigrationTaskExecutor;
import priv.ethanzhang.migration.core.manager.MigrationTaskManager;
import priv.ethanzhang.migration.core.processor.MigrationProcessor;
import priv.ethanzhang.migration.core.reader.MigrationReader;
import priv.ethanzhang.migration.core.writer.MigrationWriter;

/**
 * 任务
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
@Getter
@Setter(AccessLevel.PACKAGE)
public class MigrationTask<I, O> {

    private String taskId;

    private MigrationReader<I> reader;

    private MigrationProcessor<I, O> processor;

    private MigrationWriter<O> writer;

    private MigrationContext<I, O> context;

    private MigrationTaskExecutor<I, O> executor;

    private MigrationTaskManager manager;

    private MigrationEventDispatcher dispatcher;

    MigrationTask() {}

    public void start() {
        executor.start(this);
    }

    public void stop() {
        executor.stop(this);
    }

    public void shutDown() {
        executor.shutDown(this);
    }

    public void addSubscriber(MigrationEventSubscriber<?> subscriber) {
        dispatcher.addSubsriber(subscriber);
    }

}
