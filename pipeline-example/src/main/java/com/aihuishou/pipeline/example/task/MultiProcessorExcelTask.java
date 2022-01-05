package com.aihuishou.pipeline.example.task;

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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MultiProcessorExcelTask {

    @Test
    public void test() throws InterruptedException {

        File excel = new File("src/main/resources/demo1.xlsx");
        File target = new File("src/main/resources/demo3.txt");

        PipeTask<ExcelTask.ReaderItem, JSONObject> task = LocalPipeTaskBuilder.<ExcelTask.ReaderItem, JSONObject>newBuilder()
                .reader(new EasyExcelReader<>(excel, ExcelTask.ReaderItem.class))
                .processorChain(new ProcessorA())
                .end(new ProcessorB())
                .writer(new TextLinesWriter<>(target))
                .reportPeriod(Duration.ofSeconds(5))
                .build();

        task.addSubscriber(event -> System.out.println("lifecycle event ..."));
        task.addSubscriber(event -> System.out.println("finished event ..."), TaskFinishedEvent.class);
        task.addSubscriber(event -> System.out.println("failed event ..."), TaskFailedEvent.class);

        task.start();
        task.join();

        ThreadUtil.sleep(5, TimeUnit.SECONDS);
    }

    public static class ProcessorA implements PipeProcessor<ExcelTask.ReaderItem, String> {
        @Override
        public DataChunk<String> process(TaskContext<ExcelTask.ReaderItem, String> context, DataChunk<ExcelTask.ReaderItem> input) {
            ThreadUtil.sleep(10, TimeUnit.MILLISECONDS);
            return DataChunk.of(input.stream().map(JSON::toJSONString).collect(Collectors.toList()));
        }
    }

    public static class ProcessorB implements PipeProcessor<String, JSONObject> {
        @Override
        public DataChunk<JSONObject> process(TaskContext<String, JSONObject> context, DataChunk<String> input) {
            ThreadUtil.sleep(10, TimeUnit.MILLISECONDS);
            return DataChunk.of(input.stream().map(JSON::parseObject).collect(Collectors.toList()));
        }
    }

}
