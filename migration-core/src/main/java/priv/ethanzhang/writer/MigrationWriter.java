package priv.ethanzhang.writer;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

/**
 * 数据写入
 * @param <O> 写入类型
 */
public interface MigrationWriter<O> {

    int write(MigrationContext<?, O> context, MigrationChunk<O> output);

}
