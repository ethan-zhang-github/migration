package com.aihuishou.pipeline.core.manager;

import com.aihuishou.pipeline.core.context.TaskState;
import com.aihuishou.pipeline.core.event.*;
import com.aihuishou.pipeline.core.event.dispatcher.DisruptorTaskEventDispatcher;
import com.aihuishou.pipeline.core.event.dispatcher.TaskEventDispatcher;
import com.aihuishou.pipeline.core.event.subscriber.GenericTaskEventSubscriber;
import com.aihuishou.pipeline.core.task.PipeTask;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;

/**
 * 本地任务管理器
 */
@Slf4j
public enum LocalTaskManager implements TaskManager {

    INSTANCE;

    private TaskRegistry registry;

    private TaskEventDispatcher dispatcher;

    private AbstractTaskScheduler taskScheduler;

    {
        initialize();
    }

    @Override
    public void initialize() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
        registry = new InMemoryTaskRegistry();
        dispatcher = DisruptorTaskEventDispatcher.INSTANCE;
        taskScheduler = new LocalReporterScheduler(registry);
        taskScheduler.start();
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
        taskScheduler.shutdown();
    }

    private void addSubscribers() {
        dispatcher.addSubsriber(new GenericTaskEventSubscriber<TaskStartedEvent>() {
            @Override
            protected void subscribeInternal(TaskStartedEvent event) {
                PipeTask<?, ?> task = event.getTask();
                log.info("Task [{}] started...", task.getTaskId());
                task.getReporter().reportEvent(event);
                task.getReporter().report(task);
                registry.register(task);
            }
        });
        dispatcher.addSubsriber(new GenericTaskEventSubscriber<TaskShutdownEvent>() {
            @Override
            protected void subscribeInternal(TaskShutdownEvent event) {
                PipeTask<?, ?> task = event.getTask();
                task.getReporter().reportEvent(event);
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
                task.getReporter().reportEvent(event);
                task.getContext().getFinishTime().set(Instant.now());
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
                task.getReporter().reportEvent(event);
                task.getContext().getReaderState().set(TaskState.FAILED);
                task.getContext().getProcessorState().set(TaskState.FAILED);
                task.getContext().getWriterState().set(TaskState.FAILED);
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
                task.getReporter().reportEvent(event);
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
                task.getReporter().reportEvent(event);
            }
        });
    }

}
