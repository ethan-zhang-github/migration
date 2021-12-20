package com.aihuishou.pipeline.core.task;

import com.aihuishou.pipeline.core.config.GlobalConfig;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.context.LocalTaskParameter;
import com.aihuishou.pipeline.core.processor.PipeProcessorChain;
import com.aihuishou.pipeline.core.reader.PipeReader;
import com.aihuishou.pipeline.core.reporter.CompositeTaskReporter;
import com.aihuishou.pipeline.core.reporter.TaskReporter;
import com.aihuishou.pipeline.core.writer.PipeWriter;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractPipeTaskBuilder<I, O> {

    protected String taskId;

    protected PipeReader<I> reader;

    protected PipeProcessorChain<I, O> processorChain;

    protected PipeWriter<O> writer;

    protected LocalTaskParameter parameter;

    protected long total;

    protected Supplier<Long> totalSupplier;

    protected List<TaskReporter> reporters = new ArrayList<>();

    protected Duration reportPeriod = GlobalConfig.REPORTER.getReportPeriod();

    protected Duration timeout = GlobalConfig.LOCAL_REGISTRY.getTimeout();

    public PipeTask<I, O> build() {
        PipeTask<I, O> task = new PipeTask<>();
        task.setTaskId(Optional.ofNullable(taskId).orElse(UUID.randomUUID().toString()));
        task.setReader(reader);
        task.setProcessorChain(processorChain);
        task.setWriter(writer);
        if (CollectionUtils.isNotEmpty(reporters)) {
            task.setReporter(new CompositeTaskReporter(reporters));
        } else {
            task.setReporter(new CompositeTaskReporter(GlobalConfig.REPORTER.getDefaultReporter().get()));
        }
        customBuild(task);
        TaskContext<I, O> context = buildContext(task);
        context.setTotal(total > 0 ? total : totalSupplier != null ? totalSupplier.get() : -1L);
        context.setReportPeriod(reportPeriod);
        context.setTimeout(timeout);
        task.setContext(context);
        return task;
    }

    protected abstract void customBuild(PipeTask<I, O> task);

    protected abstract TaskContext<I, O> buildContext(PipeTask<I, O> task);
    
}
