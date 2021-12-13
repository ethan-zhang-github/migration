package priv.ethanzhang.pipeline.core.writer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import priv.ethanzhang.pipeline.core.context.DataChunk;
import priv.ethanzhang.pipeline.core.context.TaskContext;

import java.io.File;

public class EasyExcelWriter<O> extends OnceInitializedWriter<O> {

    private File file;
    private Class<O> clazz;
    private File template;
    private WriteMode writeMode;
    private String sheetName;
    private ExcelWriter writer;
    private WriteSheet sheet;

    private EasyExcelWriter() {}

    public static <T> EasyExcelWriter<T> ofNormal(File file, Class<T> clazz, String sheetName) {
        EasyExcelWriter<T> writer = new EasyExcelWriter<>();
        writer.file = file;
        writer.clazz = clazz;
        writer.writeMode = WriteMode.NORMAL;
        writer.sheetName = sheetName;
        return writer;
    }

    public static <T> EasyExcelWriter<T> ofTemplate(File file, Class<T> clazz, String sheetName, File template) {
        EasyExcelWriter<T> writer = new EasyExcelWriter<>();
        writer.file = file;
        writer.clazz = clazz;
        writer.writeMode = WriteMode.TEMPLATE;
        writer.sheetName = sheetName;
        writer.template = template;
        return writer;
    }

    public static <T> EasyExcelWriter<T> ofFill(File file, Class<T> clazz, String sheetName) {
        EasyExcelWriter<T> writer = new EasyExcelWriter<>();
        writer.file = file;
        writer.clazz = clazz;
        writer.writeMode = WriteMode.FILL;
        writer.sheetName = sheetName;
        return writer;
    }

    @Override
    public int write(TaskContext<?, O> context, DataChunk<O> output) {
        initialize(context);
        switch (writeMode) {
            case NORMAL:
            case TEMPLATE:
                writer.write(output.toList(), sheet);
                break;
            case FILL:
                writer.fill(output.toList(), sheet);
                break;
            default:
                break;
        }
        return output.size();
    }

    @Override
    protected void initializeInternal(TaskContext<?, O> context) {
        switch (writeMode) {
            case NORMAL:
                writer = EasyExcel.write(file, clazz).build();
            case TEMPLATE: case FILL:
                writer = EasyExcel.write(file, clazz).withTemplate(template).build();
            default:
                sheet = EasyExcel.writerSheet(sheetName).build();
        }
    }

    @Override
    protected void destroyInternal(TaskContext<?, O> context) {
        if (writer != null) {
            writer.finish();
        }
    }

    public enum WriteMode {
        NORMAL,
        TEMPLATE,
        FILL
    }

}
