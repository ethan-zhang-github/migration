package priv.ethanzhang.processor;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

/**
 * 数据处理
 * @param <I> 输入类型
 * @param <O> 输出类型
 */
public interface MigrationProcessor<I, O> {

    MigrationChunk<O> process(MigrationContext<I, ?> context, MigrationChunk<I> input);

}
