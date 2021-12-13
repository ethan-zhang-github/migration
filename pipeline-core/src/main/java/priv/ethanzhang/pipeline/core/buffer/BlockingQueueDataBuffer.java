package priv.ethanzhang.pipeline.core.buffer;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 数据缓冲区（基于阻塞式队列）
 * @param <T> 数据类型
 */
@Slf4j
public class BlockingQueueDataBuffer<T> implements DataBuffer<T> {

    private final int capacity;

    private final BlockingQueue<T> queue;

    public BlockingQueueDataBuffer(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean isFull() {
        return queue.size() >= capacity;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public void produce(T data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean tryProduce(T data) {
        return queue.offer(data);
    }

    @Override
    public T consume() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public List<T> consumeIfPossible(int maxSize) {
        T head = queue.poll();
        if (head == null) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(maxSize);
        list.add(head);
        queue.drainTo(list, maxSize - 1);
        return list;
    }

}
