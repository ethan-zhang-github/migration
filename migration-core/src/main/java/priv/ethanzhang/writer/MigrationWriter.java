package priv.ethanzhang.writer;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

public interface MigrationWriter<O> {

    void write(MigrationContext<?, O> context, MigrationChunk<O> output);

}
