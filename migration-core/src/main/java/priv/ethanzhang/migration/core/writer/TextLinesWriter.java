package priv.ethanzhang.migration.core.writer;

import org.apache.commons.io.FileUtils;
import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;
import priv.ethanzhang.migration.core.exception.MigrationTaskWriteException;

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
    public int write(MigrationContext<?, O> context, MigrationChunk<O> output) {
        try {
            FileUtils.writeLines(file, output.map(stringMapper).toList(), true);
            return output.size();
        } catch (IOException e) {
            throw new MigrationTaskWriteException(e);
        }
    }

    @Override
    protected void initializeInternal(MigrationContext<?, O> context) {
        file.deleteOnExit();
    }

}
