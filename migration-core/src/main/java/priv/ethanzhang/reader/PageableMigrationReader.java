package priv.ethanzhang.reader;

import priv.ethanzhang.context.MigrationChunk;
import priv.ethanzhang.context.MigrationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public abstract class PageableMigrationReader<I> implements MigrationReader<I> {

    private final AtomicLong page;

    private final long pageSize;

    private final long total;

    private final Map<Long, MigrationChunk<I>> pageCache = new HashMap<>();

    public PageableMigrationReader(long pageSize) {
        this.page = new AtomicLong(1);
        this.pageSize = pageSize;
        this.total = -1;
    }

    public PageableMigrationReader(long page, long pageSize) {
        this.page = new AtomicLong(page);
        this.pageSize = pageSize;
        this.total = -1;
    }

    public PageableMigrationReader(long page, long pageSize, long total) {
        this.page = new AtomicLong(page);
        this.pageSize = pageSize;
        this.total = total;
    }

    @Override
    public MigrationChunk<I> read(MigrationContext<I, ?> context) {
        if (!hasNextPage(context, page.get(), pageSize)) {
            return MigrationChunk.empty();
        }
        long nextPage = page.incrementAndGet();
        MigrationChunk<I> chunk;
        if (pageCache.containsKey(nextPage)) {
            chunk = pageCache.get(nextPage);
        } else {
            chunk = readNextPage(context, nextPage, pageSize);
        }
        pageCache.clear();
        return chunk;
    }

    protected boolean hasNextPage(MigrationContext<I, ?> context, long page, long pageSize) {
        if (total > -1) {
            return (page - 1) * pageSize < total;
        }
        MigrationChunk<I> chunk = readNextPage(context, page + 1, pageSize);
        pageCache.put(page + 1, chunk);
        return chunk.isNotEmpty();
    }

    protected abstract MigrationChunk<I> readNextPage(MigrationContext<I, ?> context, long page, long pageSize);

}
