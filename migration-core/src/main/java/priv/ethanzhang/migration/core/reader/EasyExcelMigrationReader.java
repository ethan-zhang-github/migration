package priv.ethanzhang.migration.core.reader;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.github.rholder.retry.*;
import org.apache.commons.collections4.CollectionUtils;
import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.event.MigrationTaskWarnningEvent;

import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EasyExcelMigrationReader<I> extends OnceInitializedMigrationReader<I> {

    private final File file;

    private final Class<I> clazz;

    private Set<Integer> sheetNos;

    private final AtomicInteger unreadSheetCount = new AtomicInteger(Integer.MAX_VALUE);

    private final BlockingQueue<I> buffer = new ArrayBlockingQueue<>(1 << 10);

    private final AtomicBoolean finished = new AtomicBoolean();

    public EasyExcelMigrationReader(File file, Class<I> clazz, int... sheetNos) {
        this.file = file;
        this.clazz = clazz;
        if (sheetNos != null) {
            this.sheetNos = Arrays.stream(sheetNos).boxed().collect(Collectors.toSet());
        }
    }

    @Override
    public MigrationChunk<I> read(MigrationContext<I, ?> context) {
        try {
            while (!finished.get()) {
                I head = buffer.poll(5, TimeUnit.SECONDS);
                if (Objects.nonNull(head)) {
                    List<I> list = new ArrayList<>(buffer.size() + 1);
                    list.add(head);
                    buffer.drainTo(list);
                    return MigrationChunk.ofList(list);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return MigrationChunk.empty();
    }

    @Override
    protected void initializeInternal(MigrationContext<I, ?> context) {
        ExcelReader excelReader = EasyExcel.read(file, clazz, new EasyExcelItemReaderListener(context)).build();
        List<ReadSheet> needReadSheet;
        if (CollectionUtils.isEmpty(sheetNos)) {
            needReadSheet = excelReader.excelExecutor().sheetList();
        } else {
            needReadSheet = excelReader.excelExecutor().sheetList().stream().filter(s -> sheetNos.contains(s.getSheetNo())).collect(Collectors.toList());
        }
        excelReader.read(needReadSheet);
        unreadSheetCount.set(needReadSheet.size());
    }

    private class EasyExcelItemReaderListener extends AnalysisEventListener<I> {

        private final MigrationContext<I, ?> migrationContext;

        private EasyExcelItemReaderListener(MigrationContext<I, ?> context) {
            this.migrationContext = context;
        }

        private final Retryer<Object> retryer = RetryerBuilder.newBuilder()
                .retryIfResult(Boolean.FALSE::equals)
                .withStopStrategy(StopStrategies.stopAfterAttempt(10))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .build();

        @Override
        public void invoke(I data, AnalysisContext context) {
            try {
                retryer.call(() -> buffer.offer(data, 5, TimeUnit.SECONDS));
            } catch (ExecutionException | RetryException e) {
                migrationContext.getTask().getDispatcher().dispatch(new MigrationTaskWarnningEvent(migrationContext.getTask(), MigrationTaskWarnningEvent.Cause.READER_TO_BUFFER_FAILED, e));
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            if (unreadSheetCount.decrementAndGet() == 0) {
                finished.set(true);
            }
        }

        @Override
        public void onException(Exception exception, AnalysisContext context) throws Exception {
            finished.set(true);
            super.onException(exception, context);
        }

    }

}
