package priv.ethanzhang.pipeline.core.reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.ethanzhang.pipeline.core.task.PipeTask;
import priv.ethanzhang.pipeline.core.task.PipeTaskAttributes;

public class LoggerTaskReporter implements TaskReporter {

    public static final LoggerTaskReporter INSTANCE = new LoggerTaskReporter();

    private LoggerTaskReporter() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTaskReporter.class);

    @Override
    public void report(PipeTask<?, ?> task) {
        PipeTaskAttributes attributes = PipeTaskAttributes.of(task);
        LOGGER.info("\n" + attributes.format("\n"));
    }

}
