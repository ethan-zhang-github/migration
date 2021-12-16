package com.aihuishou.pipeline.core.reporter;

import com.aihuishou.pipeline.core.task.PipeTask;
import com.aihuishou.pipeline.core.task.PipeTaskAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LoggerTaskReporter implements TaskReporter {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTaskReporter.class);

    @Override
    public void report(PipeTask<?, ?> task) {
        PipeTaskAttributes attributes = PipeTaskAttributes.of(task);
        LOGGER.info("\n" + attributes.format("\n"));
    }

}
