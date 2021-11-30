package priv.ethanzhang.migration.core.processor;

import priv.ethanzhang.migration.core.context.MigrationChunk;
import priv.ethanzhang.migration.core.context.MigrationContext;

/**
 * 数据处理
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
public interface MigrationProcessor<I, O> {

    MigrationChunk<O> process(MigrationContext<I, ?> context, MigrationChunk<I> input);

}
