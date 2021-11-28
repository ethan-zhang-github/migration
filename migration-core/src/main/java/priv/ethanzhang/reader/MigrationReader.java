package priv.ethanzhang.reader;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

/**
 * 数据读取
 * @param <I> 读取类型
 */
public interface MigrationReader<I> {

    MigrationChunk<I> read(MigrationContext<I, ?> context);

}
