package priv.ethanzhang.migration.core.task;

import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.context.MigrationParameter;
import priv.ethanzhang.migration.core.processor.MigrationProcessor;
import priv.ethanzhang.migration.core.reader.MigrationReader;
import priv.ethanzhang.migration.core.reporter.CompositeMigrationTaskReporter;
import priv.ethanzhang.migration.core.reporter.LoggerMigrationTaskReporter;
import priv.ethanzhang.migration.core.reporter.MigrationTaskReporter;
import priv.ethanzhang.migration.core.writer.MigrationWriter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractMigrationTaskBuilder<I, O> {

    protected String taskId;

    protected MigrationReader<I> reader;

    protected MigrationProcessor<I, O> processor;

    protected MigrationWriter<O> writer;

    protected MigrationParameter parameter;

    protected long total;

    protected Supplier<Long> totalSupplier;

    protected List<MigrationTaskReporter> reporters = new ArrayList<>();

    protected Duration reportPeriod = Duration.ofMinutes(1);

    public AbstractMigrationTaskBuilder<I, O> taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public AbstractMigrationTaskBuilder<I, O> reader(MigrationReader<I> reader) {
        this.reader = reader;
        return this;
    }

    public AbstractMigrationTaskBuilder<I, O> processor(MigrationProcessor<I, O> processor) {
        this.processor = processor;
        return this;
    }

    public AbstractMigrationTaskBuilder<I, O> writer(MigrationWriter<O> writer) {
        this.writer = writer;
        return this;
    }

    public AbstractMigrationTaskBuilder<I, O> parameter(MigrationParameter parameter) {
        this.parameter = parameter;
        return this;
    }

    public AbstractMigrationTaskBuilder<I, O> total(long total) {
        this.total = total;
        return this;
    }

    public AbstractMigrationTaskBuilder<I, O> total(Supplier<Long> totalSupplier) {
        this.totalSupplier = totalSupplier;
        return this;
    }

    public AbstractMigrationTaskBuilder<I, O> addReporter(MigrationTaskReporter reporter) {
        this.reporters.add(reporter);
        return this;
    }

    public AbstractMigrationTaskBuilder<I, O> addReporter(Duration reportPeriod) {
        this.reportPeriod = reportPeriod;
        return this;
    }

    public MigrationTask<I, O> build() {
        MigrationTask<I, O> task = new MigrationTask<>();
        task.setTaskId(taskId);
        task.setReader(reader);
        task.setProcessor(processor);
        task.setWriter(writer);
        if (CollectionUtils.isNotEmpty(reporters)) {
            task.setReporter(new CompositeMigrationTaskReporter(reporters));
        } else {
            task.setReporter(new CompositeMigrationTaskReporter(LoggerMigrationTaskReporter.INSTANCE));
        }
        customBuild(task);
        MigrationContext<I, O> context = buildContext(task);
        context.setTotal(total > 0 ? total : totalSupplier != null ? totalSupplier.get() : -1L);
        context.setReportPeriod(reportPeriod);
        task.setContext(context);
        return task;
    }

    protected abstract void customBuild(MigrationTask<I, O> task);

    protected abstract MigrationContext<I, O> buildContext(MigrationTask<I, O> task);
    
}
