package priv.ethanzhang.migration.core.context;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MigrationChunk<T> implements Iterable<T> {

    private MigrationChunk() {}

    public List<MigrationChunk<T>> partition(int size) {
        return Lists.partition(toList(), size).stream().map(MigrationChunk::of).collect(Collectors.toList());
    }

    public <V> MigrationChunk<V> map(Function<T, V> mapper) {
        return MigrationChunk.of(stream().map(mapper).collect(Collectors.toList()));
    }

    public abstract boolean isEmpty();

    public abstract boolean isNotEmpty();

    public abstract List<T> toList();

    public abstract Set<T> toSet();

    public abstract int size();

    public abstract Stream<T> stream();

    @SuppressWarnings("unchecked")
    public static <T> MigrationChunk<T> empty() {
        return (EmptyMigrationChunk<T>) EmptyMigrationChunk.INSTANCE;
    }

    public static <T> MigrationChunk<T> of(Collection<T> data) {
        return new ArrayListMigrationChunk<>(data);
    }

    private static class ArrayListMigrationChunk<T> extends MigrationChunk<T> {

        private List<T> data;

        private ArrayListMigrationChunk(Collection<T> data) {
            ArrayListMigrationChunk.this.data = new ArrayList<>(data);
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
        public Set<T> toSet() {
            return new HashSet<>(data);
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        public Stream<T> stream() {
            return data.stream();
        }

        @SuppressWarnings("all")
        @Override
        public Iterator<T> iterator() {
            return data.iterator();
        }

    }

    private static class HashSetMigrationChunk<T> extends MigrationChunk<T> {

        private Set<T> data;

        private HashSetMigrationChunk(Collection<T> data) {
            HashSetMigrationChunk.this.data = new HashSet<>(data);
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
        public Set<T> toSet() {
            return new HashSet<>(data);
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        public Stream<T> stream() {
            return data.stream();
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
        public Set<T> toSet() {
            return Collections.emptySet();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Stream<T> stream() {
            return Stream.empty();
        }

        @SuppressWarnings("all")
        @Override
        public Iterator<T> iterator() {
            return Collections.emptyIterator();
        }

    }

}
