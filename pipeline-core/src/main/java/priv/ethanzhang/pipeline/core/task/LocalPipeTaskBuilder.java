package priv.ethanzhang.pipeline.core.task;

import priv.ethanzhang.pipeline.core.buffer.DisruptorDataBuffer;
import priv.ethanzhang.pipeline.core.context.TaskParameter;
import priv.ethanzhang.pipeline.core.event.dispatcher.GuavaTaskEventDispatcher;
import priv.ethanzhang.pipeline.core.processor.PipeProcessor;
import priv.ethanzhang.pipeline.core.reader.PipeReader;
import priv.ethanzhang.pipeline.core.reporter.TaskReporter;
import priv.ethanzhang.pipeline.core.writer.PipeWriter;
import priv.ethanzhang.pipeline.core.context.LocalTaskContext;
import priv.ethanzhang.pipeline.core.context.TaskContext;
import priv.ethanzhang.pipeline.core.executor.LocalTaskExecutor;
import priv.ethanzhang.pipeline.core.manager.LocalTaskManager;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class LocalPipeTaskBuilder<I, O> extends AbstractPipeTaskBuilder<I, O> {

    private int readBufferSize = Integer.MAX_VALUE;

    private int writeBufferSize = Integer.MAX_VALUE;

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private LocalPipeTaskBuilder() {}

    public static <I, O> LocalPipeTaskBuilder<I, O> newBuilder() {
        return new LocalPipeTaskBuilder<>();
    }

    public LocalPipeTaskBuilder<I, O> taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> reader(PipeReader<I> reader) {
        this.reader = reader;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> processor(PipeProcessor<I, O> processor) {
        this.processor = processor;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> writer(PipeWriter<O> writer) {
        this.writer = writer;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> parameter(TaskParameter parameter) {
        this.parameter = parameter;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> total(long total) {
        this.total = total;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> total(Supplier<Long> totalSupplier) {
        this.totalSupplier = totalSupplier;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> addReporter(TaskReporter reporter) {
        this.reporters.add(reporter);
        return this;
    }

    public LocalPipeTaskBuilder<I, O> reportPeriod(Duration reportPeriod) {
        this.reportPeriod = reportPeriod;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    @Override
    protected void customBuild(PipeTask<I, O> task) {
        task.setExecutor(new LocalTaskExecutor<>(executor));
        task.setManager(LocalTaskManager.INSTANCE);
        task.setDispatcher(GuavaTaskEventDispatcher.INSTANCE);
    }

    @Override
    protected TaskContext<I, O> buildContext(PipeTask<I, O> task) {
        return LocalTaskContext.<I, O>builder()
                .task(task)
                .parameter(parameter)
                .readBuffer(new DisruptorDataBuffer<>(readBufferSize))
                .writeBuffer(new DisruptorDataBuffer<>(writeBufferSize))
                .build();
    }

}
