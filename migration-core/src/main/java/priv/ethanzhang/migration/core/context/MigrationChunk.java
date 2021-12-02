package priv.ethanzhang.migration.core.context;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MigrationChunk<T> implements Iterable<T> {

    private MigrationChunk() {}

    public List<MigrationChunk<T>> partition(int size) {
        return Lists.partition(toList(), size).stream().map(MigrationChunk::ofList).collect(Collectors.toList());
    }

    public abstract boolean isEmpty();

    public abstract boolean isNotEmpty();

    public abstract List<T> toList();

    public abstract int size();

    @SuppressWarnings("unchecked")
    public static <T> MigrationChunk<T> empty() {
        return (EmptyMigrationChunk<T>) EmptyMigrationChunk.INSTANCE;
    }

    public static <T> MigrationChunk<T> ofList(Collection<T> data) {
        return new ListMigrationChunk<>(data);
    }

    private static class ListMigrationChunk<T> extends MigrationChunk<T> {

        private List<T> data;

        private ListMigrationChunk(Collection<T> data) {
            ListMigrationChunk.this.data = new ArrayList<>(data);
        }

        @Override
        public boolean isEmpty() {
            return CollectionUtils.isEmpty(data);
        }

        @Override
        public boolean isNotEmpty() {
            return CollectionUtils.isNotEmpty(data);
        }

        @Override
        public List<T> toList() {
            return new ArrayList<>(data);
        }

        @Override
        public int size() {
            return data.size();
        }

        @SuppressWarnings("all")
        @Override
        public Iterator<T> iterator() {
            return data.iterator();
        }

    }

    private static class EmptyMigrationChunk<T> extends MigrationChunk<T> {

        @SuppressWarnings("rawtypes")
        private static final EmptyMigrationChunk INSTANCE = new EmptyMigrationChunk<>();

        private EmptyMigrationChunk() {}

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean isNotEmpty() {
            return false;
        }

        @Override
        public List<T> toList() {
            return Collections.emptyList();
        }

        @Override
        public int size() {
            return 0;
        }

        @SuppressWarnings("all")
        @Override
        public Iterator<T> iterator() {
            return Collections.emptyIterator();
        }

    }

}
