package com.aihuishou.core.reader;

import com.aihuishou.core.context.DataChunk;
import com.aihuishou.core.context.TaskContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分页读取
 * @param <I> 读取类型
 */
public abstract class PageableReader<I> implements PipeReader<I> {

    private final AtomicLong page;

    private final long pageSize;

    private final long total;

    private final Map<Long, DataChunk<I>> pageCache = new HashMap<>();

    public PageableReader(long pageSize) {
        this.page = new AtomicLong(1);
        this.pageSize = pageSize;
        this.total = -1;
    }

    public PageableReader(long page, long pageSize) {
        this.page = new AtomicLong(page);
        this.pageSize = pageSize;
        this.total = -1;
    }

    public PageableReader(long page, long pageSize, long total) {
        this.page = new AtomicLong(page);
        this.pageSize = pageSize;
        this.total = total;
    }

    @Override
    public DataChunk<I> read(TaskContext<I, ?> context) {
        if (!hasNextPage(context, page.get(), pageSize)) {
            return DataChunk.empty();
        }
        long nextPage = page.incrementAndGet();
        DataChunk<I> chunk;
        if (pageCache.containsKey(nextPage)) {
            chunk = pageCache.get(nextPage);
        } else {
            chunk = readNextPage(context, nextPage, pageSize);
        }
        pageCache.clear();
        return chunk;
    }

    protected boolean hasNextPage(TaskContext<I, ?> context, long page, long pageSize) {
        if (total > -1) {
            return (page - 1) * pageSize < total;
        }
        DataChunk<I> chunk = readNextPage(context, page + 1, pageSize);
        pageCache.put(page + 1, chunk);
        return chunk.isNotEmpty();
    }

    protected abstract DataChunk<I> readNextPage(TaskContext<I, ?> context, long page, long pageSize);

}
