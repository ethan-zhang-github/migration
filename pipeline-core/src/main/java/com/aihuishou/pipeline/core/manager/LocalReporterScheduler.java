package com.aihuishou.pipeline.core.manager;

import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

class LocalReporterScheduler extends AbstractScheduledService {

    private final TaskRegistry registry;

    private final ConcurrentMap<String, Instant> timer = new ConcurrentHashMap<>();

    LocalReporterScheduler(TaskRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void runOneIteration() {
        Map<String, PipeTask<?, ?>> tasks = registry.getAll();
        for (Map.Entry<String, PipeTask<?, ?>> entry : tasks.entrySet()) {
            String taskId = entry.getKey();
            PipeTask<?, ?> task = entry.getValue();
            TaskContext<?, ?> context = task.getContext();
            if (context.isTerminated() || context.isFailed()) {
                timer.remove(taskId);
                continue;
            }
            if (!timer.containsKey(taskId)) {
                try {
                    task.getReporter().report(task);
                } finally {
                    timer.put(taskId, Instant.now());
                }
            } else {
                Instant lastReportTime = timer.get(taskId);
                if (lastReportTime.plus(context.getReportPeriod()).isBefore(Instant.now())) {
                    try {
                        task.getReporter().report(task);
                    } finally {
                        timer.put(taskId, Instant.now());
                    }
                }
            }
        }
    }

    @Override
    protected void shutDown() {
        timer.clear();
    }

    @SuppressWarnings("all")
    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(5, 5, TimeUnit.SECONDS);
    }

}
