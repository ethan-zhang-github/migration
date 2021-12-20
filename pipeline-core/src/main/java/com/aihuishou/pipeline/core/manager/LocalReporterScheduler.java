package com.aihuishou.pipeline.core.manager;

import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.event.TaskEvictedEvent;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

class LocalReporterScheduler extends AbstractTaskScheduler {

    private final ConcurrentMap<String, Instant> timer = new ConcurrentHashMap<>();

    private final Scheduler scheduler = new Scheduler();

    public LocalReporterScheduler(TaskRegistry taskRegistry) {
        super(taskRegistry);
    }

    @Override
    protected void start() {
        scheduler.startAsync();
    }

    @Override
    protected void shutdown() {
        scheduler.stopAsync();
    }

    private class Scheduler extends AbstractScheduledService {

        @Override
        protected void runOneIteration() {
            Map<String, PipeTask<?, ?>> tasks = taskRegistry.getAll();
            for (Map.Entry<String, PipeTask<?, ?>> entry : tasks.entrySet()) {
                String taskId = entry.getKey();
                PipeTask<?, ?> task = entry.getValue();
                TaskContext<?, ?> context = task.getContext();
                if (context.isTerminated() || context.isFailed()) {
                    timer.remove(taskId);
                    continue;
                }
                if (context.isTimeout()) {
                    task.getDispatcher().dispatch(new TaskEvictedEvent(task, RemovalCause.EXPIRED));
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

        @SuppressWarnings("all")
        @Override
        protected Scheduler scheduler() {
            return Scheduler.newFixedRateSchedule(5, 5, TimeUnit.SECONDS);
        }

        @Override
        protected void shutDown() throws Exception {
            timer.clear();
        }

    }

}
