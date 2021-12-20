package com.aihuishou.pipeline.core.task;

import com.aihuishou.pipeline.core.processor.PipeProcessor;
import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.config.GlobalConfig;
import com.aihuishou.pipeline.core.context.LocalTaskContext;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.context.LocalTaskParameter;
import com.aihuishou.pipeline.core.executor.LocalTaskExecutor;
import com.aihuishou.pipeline.core.manager.LocalTaskManager;
import com.aihuishou.pipeline.core.processor.PipeProcessorChain;
import com.aihuishou.pipeline.core.processor.PipeProcessorNode;
import com.aihuishou.pipeline.core.reader.PipeReader;
import com.aihuishou.pipeline.core.reporter.TaskReporter;
import com.aihuishou.pipeline.core.writer.PipeWriter;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

public class LocalPipeTaskBuilder<I, O> extends AbstractPipeTaskBuilder<I, O> {

    private int readBufferSize = GlobalConfig.BUFFER.getBufferSize();

    private int writeBufferSize = GlobalConfig.BUFFER.getBufferSize();

    @SuppressWarnings("rawtypes")
    private Function<Integer, DataBuffer> dataBuffer = GlobalConfig.BUFFER.getDefaultDataBuffer();

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
        this.processorChain = new PipeProcessorChain<>(new PipeProcessorNode<>(processor));
        return this;
    }

    public <T, R> ProcessorChainBuilder<T, R> processorChain(PipeProcessor<T, R> processor, int bufferSize) {
        return new ProcessorChainBuilder<>(processor, bufferSize);
    }

    public <T, R> ProcessorChainBuilder<T, R> processorChain(PipeProcessor<T, R> processor) {
        return new ProcessorChainBuilder<>(processor, GlobalConfig.BUFFER.getBufferSize());
    }

    public LocalPipeTaskBuilder<I, O> writer(PipeWriter<O> writer) {
        this.writer = writer;
        return this;
    }

    public LocalPipeTaskBuilder<I, O> parameter(LocalTaskParameter parameter) {
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

    public LocalPipeTaskBuilder<I, O> timeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    @SuppressWarnings("rawtypes")
    public LocalPipeTaskBuilder<I, O> bufferType(Function<Integer, DataBuffer> dataBuffer) {
        this.dataBuffer = dataBuffer;
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
        task.setDispatcher(GlobalConfig.LOCAL_DISPATCHER.getDefaultDispatcher().get());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TaskContext<I, O> buildContext(PipeTask<I, O> task) {
        return LocalTaskContext.<I, O>builder()
                .task(task)
                .parameter(parameter)
                .readBuffer(dataBuffer.apply(readBufferSize))
                .writeBuffer(dataBuffer.apply(writeBufferSize))
                .build();
    }

    public class ProcessorChainBuilder<T, R> {

        private final LinkedList<PipeProcessorNode<?, ?>> nodes;

        private ProcessorChainBuilder(PipeProcessor<T, R> processor, int bufferSize) {
            this.nodes = new LinkedList<>(Collections.singleton(new PipeProcessorNode<>(processor, bufferSize)));
        }

        private ProcessorChainBuilder(LinkedList<PipeProcessorNode<?, ?>> nodes) {
            this.nodes = nodes;
        }

        public <V> ProcessorChainBuilder<T, V> then(PipeProcessor<? super R, ? extends V> processor, int bufferSize) {
            nodes.add(new PipeProcessorNode<>(processor, bufferSize));
            return new ProcessorChainBuilder<>(nodes);
        }

        public <V> ProcessorChainBuilder<T, V> then(PipeProcessor<? super R, ? extends V> processor) {
            return then(processor, GlobalConfig.BUFFER.getBufferSize());
        }

        public LocalPipeTaskBuilder<I, O> end(PipeProcessor<? super R, ? extends O> processor) {
            nodes.add(new PipeProcessorNode<>(processor));
            LocalPipeTaskBuilder.this.processorChain = new PipeProcessorChain<I, O>(nodes);
            return LocalPipeTaskBuilder.this;
        }

    }

}
