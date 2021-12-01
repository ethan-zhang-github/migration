package priv.ethanzhang.migration.core.reporter;

import priv.ethanzhang.migration.core.task.MigrationTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeMigrationTaskReporter implements MigrationTaskReporter {

    private final List<MigrationTaskReporter> delegate;

    public CompositeMigrationTaskReporter(MigrationTaskReporter... delegate) {
        this.delegate = Arrays.stream(delegate).collect(Collectors.toList());
    }

    public CompositeMigrationTaskReporter(Collection<MigrationTaskReporter> delegate) {
        this.delegate = new ArrayList<>(delegate);
    }

    @Override
    public void report(MigrationTask<?, ?> task) {
        delegate.forEach(reporter -> reporter.report(task));
    }

    public void addReporter(MigrationTaskReporter reporter) {
        delegate.add(reporter);
    }

}
