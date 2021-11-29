package priv.ethanzhang.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import priv.ethanzhang.context.MigrationContext;
import priv.ethanzhang.executor.MigrationTaskExecutor;
import priv.ethanzhang.manager.MigrationTaskManager;
import priv.ethanzhang.processor.MigrationProcessor;
import priv.ethanzhang.reader.MigrationReader;
import priv.ethanzhang.writer.MigrationWriter;

import java.util.List;

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

    private List<MigrationTaskListener> listeners;

    MigrationTask() {}

    public void start() {
        executor.execute(this);
    }

    public void stop() {
        executor.stop(this);
    }

    public void shutDown() {
        executor.shutDown(this);
    }

    public void addListener(MigrationTaskListener listener) {
        listeners.add(listener);
    }

}
