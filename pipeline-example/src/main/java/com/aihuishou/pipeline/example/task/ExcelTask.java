package com.aihuishou.pipeline.example.task;

import com.aihuishou.pipeline.core.annotation.TaskConfig;
import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;
import com.aihuishou.pipeline.core.event.TaskFailedEvent;
import com.aihuishou.pipeline.core.event.TaskFinishedEvent;
import com.aihuishou.pipeline.core.processor.PipeProcessor;
import com.aihuishou.pipeline.core.reader.EasyExcelReader;
import com.aihuishou.pipeline.core.task.LocalPipeTaskBuilder;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.aihuishou.pipeline.core.utils.ThreadUtil;
import com.aihuishou.pipeline.core.writer.TextLinesWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ExcelTask {

    @Test
    public void test() throws InterruptedException {

        File excel = new File("src/main/resources/demo1.xlsx");
        File target = new File("src/main/resources/demo2.txt");

        PipeTask<ReaderItem, JSONObject> task = LocalPipeTaskBuilder.<ReaderItem, JSONObject>newBuilder()
                .addParameter("time", System.currentTimeMillis())
                .reader(new EasyExcelReader<>(excel, ReaderItem.class))
                .processor(new Processor())
                .writer(new TextLinesWriter<>(target))
                .reportPeriod(Duration.ofSeconds(10))
                .readBufferSize(1 << 11)
                .writeBufferSize(1 << 11)
                .build();

        task.addSubscriber(event -> System.out.println("lifecycle event ..."));
        task.addSubscriber(event -> System.out.println("finished event ..." + task.getContext().getCost()), TaskFinishedEvent.class);
        task.addSubscriber(event -> System.out.println("failed event ..."), TaskFailedEvent.class);

        task.start();
        task.join();

        ThreadUtil.sleep(5, TimeUnit.SECONDS);
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
