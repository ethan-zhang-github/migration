package priv.ethanzhang.migration.core.reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.ethanzhang.migration.core.task.MigrationTask;
import priv.ethanzhang.migration.core.task.MigrationTaskAttributes;

public class LoggerMigrationTaskReporter implements MigrationTaskReporter {

    public static final LoggerMigrationTaskReporter INSTANCE = new LoggerMigrationTaskReporter();

    private LoggerMigrationTaskReporter() {}

    private final Logger logger = LoggerFactory.getLogger(LoggerMigrationTaskReporter.class);

    @Override
    public void report(MigrationTask<?, ?> task) {
        MigrationTaskAttributes attributes = MigrationTaskAttributes.of(task);
        logger.info(attributes.format("\n"));
    }

}
