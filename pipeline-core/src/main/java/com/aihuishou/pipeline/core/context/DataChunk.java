package com.aihuishou.pipeline.core.context;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据块
 * @param <T> 数据类型
 * @author ethan zhang
 */
public abstract class DataChunk<T> implements Iterable<T> {

    private DataChunk() {}

    public List<DataChunk<T>> partition(int size) {
        return Lists.partition(toList(), size).stream().map(DataChunk::of).collect(Collectors.toList());
    }

    public <V> DataChunk<V> map(Function<T, V> mapper) {
        return DataChunk.of(stream().map(mapper).collect(Collectors.toList()));
    }

    public abstract boolean isEmpty();

    public abstract boolean isNotEmpty();

    public abstract List<T> toList();

    public abstract Set<T> toSet();

    public abstract int size();

    public abstract Stream<T> stream();

    @SuppressWarnings("unchecked")
    public static <T> DataChunk<T> empty() {
        return (EmptyDataChunk<T>) EmptyDataChunk.INSTANCE;
    }

    public static <T> DataChunk<T> of(Collection<? extends T> data) {
        return new ArrayListDataChunk<>(data);
    }

    private static class ArrayListDataChunk<T> extends DataChunk<T> {

        private List<T> data;

        private ArrayListDataChunk(Collection<? extends T> data) {
            ArrayListDataChunk.this.data = new ArrayList<>(data);
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

    private static class HashSetDataChunk<T> extends DataChunk<T> {

        private Set<T> data;

        private HashSetDataChunk(Collection<T> data) {
            HashSetDataChunk.this.data = new HashSet<>(data);
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

    private static class EmptyDataChunk<T> extends DataChunk<T> {

        @SuppressWarnings("rawtypes")
        private static final EmptyDataChunk INSTANCE = new EmptyDataChunk<>();

        private EmptyDataChunk() {}

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
