package com.aihuishou.core.manager;

import com.aihuishou.core.config.GlobalConfig;
import com.aihuishou.core.context.TaskState;
import com.aihuishou.core.event.*;
import com.aihuishou.core.event.dispatcher.TaskEventDispatcher;
import com.aihuishou.core.event.subscriber.GenericTaskEventSubscriber;
import com.aihuishou.core.task.PipeTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 本地任务管理器
 */
@Slf4j
public enum LocalTaskManager implements TaskManager {

    INSTANCE;

    private TaskRegistry registry;

    private LocalReporterScheduler reporterScheduler;

    {
        initialize();
    }

    @Override
    public void initialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
        registry = new InMemoryTaskRegistry();
        reporterScheduler = new LocalReporterScheduler(registry);
        reporterScheduler.startAsync();
        addSubscribers();
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

    private void addSubscribers() {
        TaskEventDispatcher dispatcher = GlobalConfig.LOCAL_DISPATCHER.getDefaultDispatcher().get();
        dispatcher.addSubsriber(new GenericTaskEventSubscriber<TaskStartedEvent>() {
            @Override
            protected void subscribeInternal(TaskStartedEvent event) {
                PipeTask<?, ?> task = event.getTask();
                log.info("Task [{}] started...", task.getTaskId());
                task.getReporter().report(task);
                registry.register(task);
            }
        });
        dispatcher.addSubsriber(new GenericTaskEventSubscriber<TaskShutdownEvent>() {
            @Override
            protected void subscribeInternal(TaskShutdownEvent event) {
                PipeTask<?, ?> task = event.getTask();
                task.getReporter().report(task);
                task.getDispatcher().clearTaskEventStream(task.getTaskId());
                registry.unregister(task);
                log.info("Task [{}] has been shutdown...", task.getTaskId());
            }
        });
        dispatcher.addSubsriber(new GenericTaskEventSubscriber<TaskFinishedEvent>() {
            @Override
            protected void subscribeInternal(TaskFinishedEvent event) {
                PipeTask<?, ?> task = event.getTask();
                task.getReporter().report(task);
                task.getDispatcher().clearTaskEventStream(task.getTaskId());
                registry.unregister(task);
                log.info("Task [{}] finished...", task.getTaskId());
            }
        });
        dispatcher.addSubsriber(new GenericTaskEventSubscriber<TaskFailedEvent>() {
            @Override
            protected void subscribeInternal(TaskFailedEvent event) {
                PipeTask<?, ?> task = event.getTask();
                task.getContext().setReaderState(TaskState.FAILED);
                task.getContext().setProcessorState(TaskState.FAILED);
                task.getContext().setWriterState(TaskState.FAILED);
                task.getReporter().report(task);
                task.getDispatcher().clearTaskEventStream(task.getTaskId());
                registry.unregister(task);
                log.error("Task [{}] failed, cause: {}", task.getTaskId(), event.getCause(), event.getThrowable());
            }
        });
        dispatcher.addSubsriber(new GenericTaskEventSubscriber<TaskEvictedEvent>() {
            @Override
            protected void subscribeInternal(TaskEvictedEvent event) {
                PipeTask<?, ?> task = event.getTask();
                task.getReporter().report(task);
                task.getDispatcher().clearTaskEventStream(task.getTaskId());
                registry.unregister(task);
                log.info("Task [{}] has been evicted, cause: {}", task.getTaskId(), event.getCause());
            }
        });
        dispatcher.addSubsriber(new GenericTaskEventSubscriber<TaskWarnningEvent>() {
            @Override
            protected void subscribeInternal(TaskWarnningEvent event) {
                PipeTask<?, ?> task = event.getTask();
                log.warn("Task [{}] warnning, cause: {}, e: {}", task.getTaskId(), event.getCause(), event.getThrowable());
            }
        });
    }

}
