package com.aihuishou.pipeline.core.reporter;

import com.aihuishou.pipeline.core.event.TaskLifecycleEvent;
import com.aihuishou.pipeline.core.task.PipeTask;

/**
 * 任务状态报告
 * @author ethan zhang
 */
public interface TaskReporter {

    void report(PipeTask<?, ?> task);

    void reportEvent(TaskLifecycleEvent event);

}
