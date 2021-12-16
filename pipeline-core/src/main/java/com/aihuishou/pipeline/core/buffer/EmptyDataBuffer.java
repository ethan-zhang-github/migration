package com.aihuishou.pipeline.core.buffer;

import java.util.List;

class EmptyDataBuffer<T> implements DataBuffer<T> {

    EmptyDataBuffer() {}

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public void produce(T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryProduce(T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T consume() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> consumeIfPossible(int maxsize) {
        throw new UnsupportedOperationException();
    }

}
