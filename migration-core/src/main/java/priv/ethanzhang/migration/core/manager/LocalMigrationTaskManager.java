package priv.ethanzhang.migration.core.manager;

import lombok.extern.slf4j.Slf4j;
import priv.ethanzhang.migration.core.context.MigrationState;
import priv.ethanzhang.migration.core.event.*;
import priv.ethanzhang.migration.core.task.MigrationTask;

import java.util.Map;

/**
 * 本地任务管理器
 */
@Slf4j
public class LocalMigrationTaskManager implements MigrationTaskManager {

    public static final LocalMigrationTaskManager INSTANCE = new LocalMigrationTaskManager();

    private MigrationTaskRegistry registry;

    private LocalReporterScheduler reporterScheduler;

    private LocalMigrationTaskManager() {
        initialize();
    }

    @Override
    public void initialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
        registry = new InMemoryMigrationTaskRegistry();
        reporterScheduler = new LocalReporterScheduler(registry);
        reporterScheduler.startAsync();
        LocalTaskEventDispatcher.INSTANCE.addSubsriber(new LocalTaskTaskManagerSubscriber());
    }

    @Override
    public void shutDown() {
        Map<String, MigrationTask<?, ?>> migrationTaskMap = registry.getAll();
        migrationTaskMap.forEach(((taskId, task) -> {
            task.shutDown();
            task.getDispatcher().clearEventStream(taskId);
            log.warn("Task [{}] has been shut down because local migration task manager has been shut down!", taskId);
        }));
        registry.clear();
        reporterScheduler.stopAsync();
    }

    private class LocalTaskTaskManagerSubscriber implements TaskEventSubscriber<TaskTaskLifecycleEvent> {

        @Override
        public void subscribe(TaskTaskLifecycleEvent event) {
            MigrationTask<?, ?> task = event.getTask();
            if (event instanceof TaskTaskStartedEvent) {
                log.info("Task [{}] started...", task.getTaskId());
                task.getReporter().report(task);
                registry.register(task);
            }
            if (event instanceof TaskTaskShutdownEvent) {
                log.info("Task [{}] has been shutdown...", task.getTaskId());
                task.getReporter().report(task);
                task.getDispatcher().clearEventStream(task.getTaskId());
                registry.unregister(task);
            }
            if (event instanceof TaskTaskFinishedEvent) {
                log.info("Task [{}] finished...", task.getTaskId());
                task.getReporter().report(task);
                task.getDispatcher().clearEventStream(task.getTaskId());
                registry.unregister(task);
            }
            if (event instanceof TaskTaskFailedEvent) {
                log.error("Task [{}] failed...", task.getTaskId());
                task.getContext().setReaderState(MigrationState.FAILED);
                task.getContext().setProcessorState(MigrationState.FAILED);
                task.getContext().setWriterState(MigrationState.FAILED);
                task.getReporter().report(task);
                task.getDispatcher().clearEventStream(task.getTaskId());
                registry.unregister(task);
            }
        }

    }

}
