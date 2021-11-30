package priv.ethanzhang.migration.core.writer;

import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;

/**
 * 数据写入
 * @param <O> 写入类型
 */
public interface MigrationWriter<O> {

    int write(MigrationContext<?, O> context, MigrationChunk<O> output);

}
