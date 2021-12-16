package com.aihuishou.core.reporter;

import com.aihuishou.core.task.PipeTask;
import com.aihuishou.core.task.PipeTaskAttributes;
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
