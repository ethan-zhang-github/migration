package com.aihuishou.pipeline.core.reader;

import com.aihuishou.pipeline.core.context.DataChunk;
import com.aihuishou.pipeline.core.context.TaskContext;
import lombok.Getter;

import java.util.function.BiFunction;

/**
 * 范围读取
 * @param <I> 读取类型
 * @param <BORDER> 边界类型
 * @param <SPAN> 跨度类型
 */
@Getter
public abstract class RangedReader<I, BORDER extends Comparable<BORDER>, SPAN> implements PipeReader<I> {

    /**
     * 左边界
     */
    private final BORDER left;

    /**
     * 右边界
     */
    private final BORDER right;

    /**
     * 步长
     */
    private SPAN span;

    /**
     * 游标移动逻辑
     */
    private final BiFunction<BORDER, SPAN, BORDER> incrementer;

    /**
     * 游标
     */
    private BORDER cursor;

    public RangedReader(BORDER left, BORDER right, SPAN span, BiFunction<BORDER, SPAN, BORDER> incrementer) {
        this.left = left;
        this.right = right;
        this.span = span;
        this.incrementer = incrementer;
        this.cursor = left;
    }

    public RangedReader(BORDER left, BORDER right) {
        this.left = left;
        this.right = right;
        this.incrementer = (l, s) -> right;
        this.cursor = left;
    }

    @Override
    public DataChunk<I> read(TaskContext<I, ?> context) {
        // 游标到达右边界，则跳出循环
        while (cursor.compareTo(right) < 0) {
            BORDER nextCursor = incrementer.apply(cursor, span);
            // 游标下一个位置超出右边界，则置为右边界
            if (nextCursor.compareTo(right) > 0) {
                nextCursor = right;
            }
            DataChunk<I> chunk = readRange(context, cursor, nextCursor);
            cursor = nextCursor;
            if (chunk.isNotEmpty()) {
                return chunk;
            }
        }
        return DataChunk.empty();
    }

    protected abstract DataChunk<I> readRange(TaskContext<I, ?> context, BORDER left, BORDER right);

}
