package com.aihuishou.core.task;

import org.apache.commons.collections4.CollectionUtils;
import com.aihuishou.core.config.GlobalConfig;
import com.aihuishou.core.context.TaskContext;
import com.aihuishou.core.context.TaskParameter;
import com.aihuishou.core.processor.PipeProcessorChain;
import com.aihuishou.core.reader.PipeReader;
import com.aihuishou.core.reporter.CompositeTaskReporter;
import com.aihuishou.core.reporter.TaskReporter;
import com.aihuishou.core.writer.PipeWriter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractPipeTaskBuilder<I, O> {

    protected String taskId;

    protected PipeReader<I> reader;

    protected PipeProcessorChain<I, O> processorChain;

    protected PipeWriter<O> writer;

    protected TaskParameter parameter;

    protected long total;

    protected Supplier<Long> totalSupplier;

    protected List<TaskReporter> reporters = new ArrayList<>();

    protected Duration reportPeriod = GlobalConfig.REPORTER.getReportPeriod();

    protected Duration timeout = Duration.ofSeconds(GlobalConfig.LOCAL_REGISTRY.getExpireSeconds());

    public PipeTask<I, O> build() {
        PipeTask<I, O> task = new PipeTask<>();
        task.setTaskId(taskId);
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
