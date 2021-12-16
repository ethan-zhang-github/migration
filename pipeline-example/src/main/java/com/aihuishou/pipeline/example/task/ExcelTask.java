package com.aihuishou.pipeline.example.task;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.junit.Test;
import com.aihuishou.core.annotation.TaskConfig;
import com.aihuishou.core.context.DataChunk;
import com.aihuishou.core.context.TaskContext;
import com.aihuishou.core.event.TaskFailedEvent;
import com.aihuishou.core.event.TaskFinishedEvent;
import com.aihuishou.core.processor.PipeProcessor;
import com.aihuishou.core.reader.EasyExcelReader;
import com.aihuishou.core.task.LocalPipeTaskBuilder;
import com.aihuishou.core.task.PipeTask;
import com.aihuishou.core.utils.ThreadUtil;
import com.aihuishou.core.writer.TextLinesWriter;

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

        task.addSubscriber(event -> System.out.println("lifecycle event ..."));
        task.addSubscriber(event -> System.out.println("finished event ..." + task.getContext().getCost()), TaskFinishedEvent.class);
        task.addSubscriber(event -> System.out.println("failed event ..."), TaskFailedEvent.class);

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

    @TaskConfig(interruptFor = IllegalArgumentException.class)
    public static class Processor implements PipeProcessor<ReaderItem, JSONObject> {

        @Override
        public DataChunk<JSONObject> process(TaskContext<ReaderItem, JSONObject> context, DataChunk<ReaderItem> input) {
            ThreadUtil.sleep(20, TimeUnit.MILLISECONDS);
            return DataChunk.of(input.stream().map(i -> JSON.parseObject(JSON.toJSONString(i))).collect(Collectors.toList()));
        }

    }

}
