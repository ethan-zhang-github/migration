package priv.ethanzhang.pipeline.core.manager;

import lombok.extern.slf4j.Slf4j;
import priv.ethanzhang.pipeline.core.context.TaskState;
import priv.ethanzhang.pipeline.core.event.*;
import priv.ethanzhang.pipeline.core.event.dispatcher.GuavaTaskEventDispatcher;
import priv.ethanzhang.pipeline.core.event.subscriber.GenericTaskEventSubscriber;
import priv.ethanzhang.pipeline.core.task.PipeTask;

import java.util.Map;

/**
 * 本地任务管理器
 */
@Slf4j
public class LocalTaskManager implements TaskManager {

    public static final LocalTaskManager INSTANCE = new LocalTaskManager();

    private TaskRegistry registry;

    private LocalReporterScheduler reporterScheduler;

    private LocalTaskManager() {
        initialize();
    }

    @Override
    public void initialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
        registry = new InMemoryTaskRegistry();
        reporterScheduler = new LocalReporterScheduler(registry);
        reporterScheduler.startAsync();
        GuavaTaskEventDispatcher.INSTANCE.addSubsriber(new LocalTaskTaskManagerSubscriber());
    }

    @Override
    public void shutDown() {
        Map<String, PipeTask<?, ?>> migrationTaskMap = registry.getAll();
        migrationTaskMap.forEach(((taskId, task) -> {
            task.shutDown();
            task.getDispatcher().clearTaskEventStream(taskId);
            log.warn("Task [{}] has been shut down because local migration task manager has been shut down!", taskId);
        }));
        registry.clear();
        reporterScheduler.stopAsync();
    }

    private class LocalTaskTaskManagerSubscriber extends GenericTaskEventSubscriber<TaskLifecycleEvent> {

        @Override
        public void subscribeInternal(TaskLifecycleEvent event) {
            PipeTask<?, ?> task = event.getTask();
            if (event instanceof TaskStartedEvent) {
                log.info("Task [{}] started...", task.getTaskId());
                task.getReporter().report(task);
                registry.register(task);
            }
            if (event instanceof TaskShutdownEvent) {
                log.info("Task [{}] has been shutdown...", task.getTaskId());
                task.getReporter().report(task);
                task.getDispatcher().clearTaskEventStream(task.getTaskId());
                registry.unregister(task);
            }
            if (event instanceof TaskFinishedEvent) {
                log.info("Task [{}] finished...", task.getTaskId());
                task.getReporter().report(task);
                task.getDispatcher().clearTaskEventStream(task.getTaskId());
                registry.unregister(task);
            }
            if (event instanceof TaskFailedEvent) {
                log.error("Task [{}] failed...", task.getTaskId());
                task.getContext().setReaderState(TaskState.FAILED);
                task.getContext().setProcessorState(TaskState.FAILED);
                task.getContext().setWriterState(TaskState.FAILED);
                task.getReporter().report(task);
                task.getDispatcher().clearTaskEventStream(task.getTaskId());
                registry.unregister(task);
            }
        }

    }

}
