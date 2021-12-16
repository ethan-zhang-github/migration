package com.aihuishou.core.reporter;

import com.aihuishou.core.task.PipeTask;

/**
 * 任务状态报告
 * @author ethan zhang
 */
public interface TaskReporter {

    void report(PipeTask<?, ?> task);

}
