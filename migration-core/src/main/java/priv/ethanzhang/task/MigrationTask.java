package priv.ethanzhang.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import priv.ethanzhang.context.MigrationContext;
import priv.ethanzhang.event.MigrationTaskShutdownEvent;
import priv.ethanzhang.event.MigrationTaskStartedEvent;
import priv.ethanzhang.event.MigrationTaskStoppedEvent;
import priv.ethanzhang.executor.MigrationTaskExecutor;
import priv.ethanzhang.manager.MigrationTaskManager;
import priv.ethanzhang.processor.MigrationProcessor;
import priv.ethanzhang.reader.MigrationReader;
import priv.ethanzhang.writer.MigrationWriter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class MigrationTask<I, O> {

    private String taskId;

    private MigrationReader<I> reader;

    private MigrationProcessor<I, O> processor;

    private MigrationWriter<O> writer;

    private MigrationContext<I, O> context;

    private MigrationTaskExecutor executor;

    private MigrationTaskManager manager;

    MigrationTask() {}

    public void start() {
        manager.publishEvent(new MigrationTaskStartedEvent(this));
    }

    public void stop() {
        manager.publishEvent(new MigrationTaskStoppedEvent(this));
    }

    public void shutDown() {
        manager.publishEvent(new MigrationTaskShutdownEvent(this));
    }

}
