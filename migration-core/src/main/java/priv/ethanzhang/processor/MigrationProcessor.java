package priv.ethanzhang.processor;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

public interface MigrationProcessor<I, O> {

    MigrationChunk<O> process(MigrationContext<I, ?> context, MigrationChunk<I> input);

}
