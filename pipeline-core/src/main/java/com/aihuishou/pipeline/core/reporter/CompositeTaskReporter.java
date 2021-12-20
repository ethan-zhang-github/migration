package com.aihuishou.pipeline.core.reporter;

import com.aihuishou.pipeline.core.event.TaskLifecycleEvent;
import com.aihuishou.pipeline.core.task.PipeTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeTaskReporter implements TaskReporter {

    private final List<TaskReporter> delegate;

    public CompositeTaskReporter(TaskReporter... delegate) {
        this.delegate = Arrays.stream(delegate).collect(Collectors.toList());
    }

    public CompositeTaskReporter(Collection<TaskReporter> delegate) {
        this.delegate = new ArrayList<>(delegate);
    }

    @Override
    public void report(PipeTask<?, ?> task) {
        delegate.forEach(reporter -> reporter.report(task));
    }

    @Override
    public void reportEvent(TaskLifecycleEvent event) {
        delegate.forEach(reporter -> reporter.reportEvent(event));
    }

    public void addReporter(TaskReporter reporter) {
        delegate.add(reporter);
    }

}
