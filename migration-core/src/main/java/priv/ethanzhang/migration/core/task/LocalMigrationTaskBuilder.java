package priv.ethanzhang.migration.core.task;

import priv.ethanzhang.migration.core.buffer.LocalMigrationBuffer;
import priv.ethanzhang.migration.core.context.LocalMigrationContext;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.context.MigrationParameter;
import priv.ethanzhang.migration.core.event.LocalMigrationEventDispatcher;
import priv.ethanzhang.migration.core.executor.LocalMigrationTaskExecutor;
import priv.ethanzhang.migration.core.manager.LocalMigrationTaskManager;
import priv.ethanzhang.migration.core.processor.MigrationProcessor;
import priv.ethanzhang.migration.core.reader.MigrationReader;
import priv.ethanzhang.migration.core.reporter.MigrationTaskReporter;
import priv.ethanzhang.migration.core.writer.MigrationWriter;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class LocalMigrationTaskBuilder<I, O> extends AbstractMigrationTaskBuilder<I, O> {

    private int readBufferSize = Integer.MAX_VALUE;

    private int writeBufferSize = Integer.MAX_VALUE;

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private LocalMigrationTaskBuilder() {}

    public static <I, O> LocalMigrationTaskBuilder<I, O> newBuilder() {
        return new LocalMigrationTaskBuilder<>();
    }

    public LocalMigrationTaskBuilder<I, O> taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> reader(MigrationReader<I> reader) {
        this.reader = reader;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> processor(MigrationProcessor<I, O> processor) {
        this.processor = processor;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> writer(MigrationWriter<O> writer) {
        this.writer = writer;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> parameter(MigrationParameter parameter) {
        this.parameter = parameter;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> total(long total) {
        this.total = total;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> total(Supplier<Long> totalSupplier) {
        this.totalSupplier = totalSupplier;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> addReporter(MigrationTaskReporter reporter) {
        this.reporters.add(reporter);
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> reportPeriod(Duration reportPeriod) {
        this.reportPeriod = reportPeriod;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }

    public LocalMigrationTaskBuilder<I, O> executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    protected void customBuild(MigrationTask<I, O> task) {
        task.setExecutor(new LocalMigrationTaskExecutor<>(executor));
        task.setManager(LocalMigrationTaskManager.INSTANCE);
        task.setDispatcher(LocalMigrationEventDispatcher.INSTANCE);
    }

    @Override
    protected MigrationContext<I, O> buildContext(MigrationTask<I, O> task) {
        return LocalMigrationContext.<I, O>builder()
                .task(task)
                .parameter(parameter)
                .readBuffer(new LocalMigrationBuffer<>(readBufferSize))
                .writeBuffer(new LocalMigrationBuffer<>(writeBufferSize))
                .build();
    }

}
