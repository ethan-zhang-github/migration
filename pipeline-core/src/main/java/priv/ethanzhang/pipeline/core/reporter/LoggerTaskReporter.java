package priv.ethanzhang.pipeline.core.reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.ethanzhang.pipeline.core.task.PipeTask;
import priv.ethanzhang.pipeline.core.task.PipeTaskAttributes;

public enum LoggerTaskReporter implements TaskReporter {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTaskReporter.class);

    @Override
    public void report(PipeTask<?, ?> task) {
        PipeTaskAttributes attributes = PipeTaskAttributes.of(task);
        LOGGER.info("\n" + attributes.format("\n"));
    }

}
