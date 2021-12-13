package priv.ethanzhang.pipeline.example.task;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.junit.Test;
import priv.ethanzhang.pipeline.core.annotation.TaskConfig;
import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskContext;
import priv.ethanzhang.pipeline.core.event.TaskFinishedEvent;
import priv.ethanzhang.pipeline.core.processor.PipeProcessor;
import priv.ethanzhang.pipeline.core.reader.EasyExcelReader;
import priv.ethanzhang.pipeline.core.task.LocalPipeTaskBuilder;
import priv.ethanzhang.pipeline.core.task.PipeTask;
import priv.ethanzhang.pipeline.core.writer.TextLinesWriter;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ExcelTask {

    @Test
    public void test() throws InterruptedException {

        File excel = new File("src/main/resources/demo1.xlsx");
        File target = new File("src/main/resources/demo2.txt");

        PipeTask<ReaderItem, JSONObject> task = LocalPipeTaskBuilder.<ReaderItem, JSONObject>newBuilder()
                .taskId(String.valueOf(System.currentTimeMillis()))
                .reader(new EasyExcelReader<>(excel, ReaderItem.class))
                .processor(new Processor())
                .writer(new TextLinesWriter<>(target))
                .reportPeriod(Duration.ofSeconds(5))
                .readBufferSize(1000)
                .writeBufferSize(1000)
                .build();

        task.addSubscriber(event -> System.out.println("finish1"), TaskFinishedEvent.class);
        task.addSubscriber(event -> System.out.println("finish2"), TaskFinishedEvent.class);

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

    @TaskConfig(ignoreFor = IllegalArgumentException.class)
    public static class Processor implements PipeProcessor<ReaderItem, JSONObject> {

        @Override
        public DataChunk<JSONObject> process(TaskContext<ReaderItem, ?> context, DataChunk<ReaderItem> input) {
            try {
                new CountDownLatch(1).await(1, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*if (Math.random() > 0.9d) {
                throw new IllegalArgumentException("test");
            }*/
            return DataChunk.of(input.stream().map(i -> JSON.parseObject(JSON.toJSONString(i))).collect(Collectors.toList()));
        }

    }

}
