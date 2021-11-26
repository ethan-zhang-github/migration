package priv.ethanzhang.reader;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

public interface MigrationReader<I> {

    MigrationChunk<I> read(MigrationContext<I, ?> context);

}
