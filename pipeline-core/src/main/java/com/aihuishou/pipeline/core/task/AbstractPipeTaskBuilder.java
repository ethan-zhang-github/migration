package com.aihuishou.pipeline.core.task;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.config.GlobalConfig;
import com.aihuishou.pipeline.core.context.TaskParameter;
import com.aihuishou.pipeline.core.context.TaskStateHolder;
import com.aihuishou.pipeline.core.processor.PipeProcessor;
import com.aihuishou.pipeline.core.processor.PipeProcessorChain;
import com.aihuishou.pipeline.core.processor.PipeProcessorNode;
import com.aihuishou.pipeline.core.reader.PipeReader;
import com.aihuishou.pipeline.core.reporter.CompositeTaskReporter;
import com.aihuishou.pipeline.core.reporter.TaskReporter;
import com.aihuishou.pipeline.core.writer.PipeWriter;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractPipeTaskBuilder<I, O, C extends AbstractPipeTaskBuilder<I, O, C>> {

    protected String taskId;

    protected TaskParameter parameter;

    protected PipeReader<I> reader;

    protected PipeProcessorChain<I, O> processorChain;

    protected PipeWriter<O> writer;

    protected int readBufferSize = GlobalConfig.BUFFER.getBufferSize();

    protected int writeBufferSize = GlobalConfig.BUFFER.getBufferSize();

    protected int processBufferSize = GlobalConfig.BUFFER.getBufferSize();

    protected long total;

    protected Supplier<Long> totalSupplier;

    protected CompositeTaskReporter reporter = new CompositeTaskReporter(GlobalConfig.REPORTER.getDefaultReporter().get());

    protected Duration reportPeriod = GlobalConfig.REPORTER.getReportPeriod();

    protected Duration timeout = GlobalConfig.LOCAL_REGISTRY.getTimeout();

    @SuppressWarnings("rawtypes")
    protected Function<Integer, DataBuffer> dataBufferGenerator;

    protected Supplier<TaskStateHolder> taskStateGenerator;

    protected Executor executor = Executors.newCachedThreadPool();

    @SuppressWarnings("rawtypes")
    protected AbstractPipeTaskBuilder(Supplier<TaskParameter> parameterGenerator, Function<Integer, DataBuffer> dataBufferGenerator,
                                      Supplier<TaskStateHolder> taskStateGenerator) {
        this.parameter = parameterGenerator.get();
        this.dataBufferGenerator = dataBufferGenerator;
        this.taskStateGenerator = taskStateGenerator;
    }

    @SuppressWarnings("unchecked")
    public C taskId(String taskId) {
        this.taskId = taskId;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C addParameter(String key, Object value) {
        this.parameter.addParameter(key, value);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C addParameters(Map<String, Object> parameters) {
        this.parameter.addParameters(parameters);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C executor(Executor executor) {
        this.executor = executor;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C reader(PipeReader<I> reader) {
        this.reader = reader;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C processor(PipeProcessor<I, O> processor) {
        this.processorChain = new PipeProcessorChain<I, O>(PipeProcessorNode.<I, O>builder()
                .processor(processor)
                .state(taskStateGenerator.get())
                .buffer(DataBuffer.EMPTY_BUFFER)
                .build());
        return (C) this;
    }

    public <T, R> ProcessorChainBuilder<T, R> processorChain(PipeProcessor<T, R> processor) {
        return new ProcessorChainBuilder<>(processor);
    }

    @SuppressWarnings("unchecked")
    public C writer(PipeWriter<O> writer) {
        this.writer = writer;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C readBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C writeBufferSize(int writeBufferSize) {
        this.writeBufferSize = writeBufferSize;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C processBufferSize(int processBufferSize) {
        this.processBufferSize = processBufferSize;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C total(long total) {
        this.total = total;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C total(Supplier<Long> totalSupplier) {
        this.totalSupplier = totalSupplier;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C addReporter(TaskReporter reporter) {
        this.reporter.addReporter(reporter);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C reportPeriod(Duration reportPeriod) {
        this.reportPeriod = reportPeriod;
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C timeout(Duration timeout) {
        this.timeout = timeout;
        return (C) this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public C useBuffer(Function<Integer, DataBuffer> dataBufferGenerator) {
        this.dataBufferGenerator = dataBufferGenerator;
        return (C) this;
    }

    public PipeTask<I, O> build() {
        PipeTask<I, O> task = new PipeTask<>();
        initialize(task);
        return task;
    }

    protected abstract void initialize(PipeTask<I, O> task);

    public class ProcessorChainBuilder<T, R> {

        @SuppressWarnings("rawtypes")
        private final LinkedList<PipeProcessorNode> nodes;

        @SuppressWarnings("unchecked")
        private ProcessorChainBuilder(PipeProcessor<T, R> processor) {
            this.nodes = new LinkedList<>(Collections.singleton(PipeProcessorNode.<T, R>builder()
                    .processor(processor)
                    .state(taskStateGenerator.get())
                    .buffer(dataBufferGenerator.apply(processBufferSize))
                    .build()));
        }

        @SuppressWarnings("rawtypes")
        private ProcessorChainBuilder(LinkedList<PipeProcessorNode> nodes) {
            this.nodes = nodes;
        }

        @SuppressWarnings("unchecked")
        public <V> ProcessorChainBuilder<T, V> then(PipeProcessor<? super R, ? extends V> processor) {
            nodes.add(PipeProcessorNode.<R, V>builder()
                    .processor(processor)
                    .state(taskStateGenerator.get())
                    .buffer(dataBufferGenerator.apply(processBufferSize))
                    .build());
            return new ProcessorChainBuilder<>(nodes);
        }

        @SuppressWarnings("unchecked")
        public C end(PipeProcessor<? super R, ? extends O> processor) {
            nodes.add(PipeProcessorNode.<R, O>builder()
                    .processor(processor)
                    .state(taskStateGenerator.get())
                    .buffer(DataBuffer.EMPTY_BUFFER)
                    .build());
            AbstractPipeTaskBuilder.this.processorChain = new PipeProcessorChain<I, O>(nodes);
            return (C) AbstractPipeTaskBuilder.this;
        }

    }

}
