package priv.ethanzhang.pipeline.core.reporter;

import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 任务状态报告
 */
public interface TaskReporter {

    void report(PipeTask<?, ?> task);

}
