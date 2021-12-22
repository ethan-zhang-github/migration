package com.aihuishou.pipeline.core.writer;

import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;
import org.apache.commons.io.FileUtils;
import com.aihuishou.pipeline.core.exception.TaskWriteException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

public class TextLinesWriter<O> extends OnceInitializedWriter<O> {

    private final File file;

    private final Function<O, String> stringMapper;

    public TextLinesWriter(File file) {
        this.file = file;
        this.stringMapper = Objects::toString;
    }

    public TextLinesWriter(File file, Function<O, String> stringMapper) {
        this.file = file;
        this.stringMapper = stringMapper;
    }

    @Override
    public int write(TaskContext<?, O> context, DataChunk<O> output) {
        try {
            FileUtils.writeLines(file, output.map(stringMapper).toList(), true);
            return output.size();
        } catch (IOException e) {
            throw new TaskWriteException(e);
        }
    }

    @SuppressWarnings("all")
    @Override
    protected void initializeInternal(TaskContext<?, O> context) {
        if (file.exists()) {
            file.delete();
        }
    }

}
