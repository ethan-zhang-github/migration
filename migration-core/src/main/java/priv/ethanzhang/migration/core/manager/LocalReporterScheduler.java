package priv.ethanzhang.migration.core.manager;

import com.google.common.util.concurrent.AbstractScheduledService;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class LocalReporterScheduler extends AbstractScheduledService {

    private final MigrationTaskRegistry registry;

    private final ConcurrentMap<String, Instant> timer = new ConcurrentHashMap<>();

    LocalReporterScheduler(MigrationTaskRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void runOneIteration() {
        Map<String, MigrationTask<?, ?>> tasks = registry.getAll();
        for (Map.Entry<String, MigrationTask<?, ?>> entry : tasks.entrySet()) {
            String taskId = entry.getKey();
            MigrationTask<?, ?> task = entry.getValue();
            MigrationContext<?, ?> context = task.getContext();
            if (context.isTerminated() || context.isFailed()) {
                timer.remove(taskId);
                continue;
            }
            if (!timer.containsKey(taskId)) {
                task.getReporter().report(task);
                timer.put(taskId, Instant.now());
            } else {
                Instant lastReportTime = timer.get(taskId);
                if (lastReportTime.plus(context.getReportPeriod()).isBefore(Instant.now())) {
                    task.getReporter().report(task);
                    timer.put(taskId, Instant.now());
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
        return Scheduler.newFixedRateSchedule(Duration.ofSeconds(5), Duration.ofSeconds(5));
    }

}
