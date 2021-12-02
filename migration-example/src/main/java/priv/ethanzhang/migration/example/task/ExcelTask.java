package priv.ethanzhang.migration.example.task;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.junit.Test;
import priv.ethanzhang.migration.core.annotation.MigrationConfig;
import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.processor.MigrationProcessor;
import priv.ethanzhang.migration.core.reader.EasyExcelReader;
import priv.ethanzhang.migration.core.task.LocalMigrationTaskBuilder;
import priv.ethanzhang.migration.core.task.MigrationTask;
import priv.ethanzhang.migration.core.writer.TextLinesWriter;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ExcelTask {

    @Test
    public void test() throws InterruptedException {

        File excel = new File("/Users/ethanzhang/IdeaProjects/migration/migration-example/src/main/resources/demo1.xlsx");
        File target = new File("/Users/ethanzhang/IdeaProjects/migration/migration-example/src/main/resources/demo2.txt");

        MigrationTask<ReaderItem, JSONObject> task = LocalMigrationTaskBuilder.<ReaderItem, JSONObject>newBuilder()
                .taskId(String.valueOf(System.currentTimeMillis()))
                .reader(new EasyExcelReader<>(excel, ReaderItem.class))
                .processor(new Processor())
                .writer(new TextLinesWriter<>(target))
                .reportPeriod(Duration.ofSeconds(5))
                .readBufferSize(1000)
                .writeBufferSize(1000)
                .build();

        task.start();

        new CountDownLatch(1).await(1, TimeUnit.HOURS);
    }

    @Data
    public static class ReaderItem {

        @ExcelProperty("trade_order_no")
        private String tradeOrderNo;

        @ExcelProperty("status")
        private String status;

        @ExcelProperty("remark")
        private String remark;

        @ExcelProperty("create_dt")
        private String createDt;

    }

//    @MigrationConfig(ignoreFor = IllegalArgumentException.class)
    public static class Processor implements MigrationProcessor<ReaderItem, JSONObject> {

        @Override
        public MigrationChunk<JSONObject> process(MigrationContext<ReaderItem, ?> context, MigrationChunk<ReaderItem> input) {
            try {
                new CountDownLatch(1).await(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            if (Math.random() > 0.9d) {
//                throw new IllegalArgumentException("test");
//            }
            return MigrationChunk.of(input.stream().map(i -> JSON.parseObject(JSON.toJSONString(i))).collect(Collectors.toList()));
        }

    }

}
