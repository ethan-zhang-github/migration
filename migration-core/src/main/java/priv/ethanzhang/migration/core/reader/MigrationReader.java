package priv.ethanzhang.migration.core.reader;

import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;

/**
 * 数据读取
 * @param <I> 读取类型
 */
public interface MigrationReader<I> {

    MigrationChunk<I> read(MigrationContext<I, ?> context);

}
