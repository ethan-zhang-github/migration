package com.aihuishou.pipeline.core.task;

import com.aihuishou.pipeline.core.event.TaskLifecycleEvent;
import com.aihuishou.pipeline.core.event.subscriber.PipeTaskEventSubscriber;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.event.dispatcher.TaskEventDispatcher;
import com.aihuishou.pipeline.core.executor.TaskExecutor;
import com.aihuishou.pipeline.core.manager.TaskManager;
import com.aihuishou.pipeline.core.processor.PipeProcessorChain;
import com.aihuishou.pipeline.core.reader.PipeReader;
import com.aihuishou.pipeline.core.reporter.TaskReporter;
import com.aihuishou.pipeline.core.writer.PipeWriter;

import java.util.function.Consumer;

/**
 * 流水线任务
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
@Getter
@Setter(AccessLevel.PACKAGE)
public class PipeTask<I, O> {

    private String taskId;

    private PipeReader<I> reader;

    private PipeProcessorChain<I, O> processorChain;

    private PipeWriter<O> writer;

    private TaskContext<I, O> context;

    private TaskExecutor<I, O> executor;

    private TaskManager manager;

    private TaskEventDispatcher dispatcher;

    private TaskReporter reporter;

    PipeTask() {}

    public void start() {
        executor.start(this);
    }

    public void stop() {
        executor.stop(this);
    }

    public void shutDown() {
        executor.shutDown(this);
    }

    public void join() {
        executor.join(this);
    }

    public void addSubscriber(Consumer<TaskLifecycleEvent> subscriber) {
        addSubscriber(subscriber, TaskLifecycleEvent.class);
    }

    public <E extends TaskLifecycleEvent> void addSubscriber(Consumer<E> subscriber, Class<E> eventType) {
        dispatcher.addSubsriber(new PipeTaskEventSubscriber<E>(this, eventType) {
            @Override
            protected void subscribeInternal(E event) {
                subscriber.accept(event);
            }
        });
    }

}
