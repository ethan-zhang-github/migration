package priv.ethanzhang.pipeline.core.reporter;

import priv.ethanzhang.pipeline.core.task.PipeTask;

/**
 * 任务状态报告
 * @author ethan zhang
 */
public interface TaskReporter {

    void report(PipeTask<?, ?> task);

}
