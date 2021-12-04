package priv.ethanzhang.migration.core.buffer;

import com.google.common.util.concurrent.Monitor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 数据缓冲区（基于阻塞式队列）
 * @param <T> 数据类型
 */
@Slf4j
public class BlockingQueueDataBuffer<T> implements DataBuffer<T> {

    private final int capacity;

    private final BlockingQueue<T> queue;

    @SuppressWarnings("all")
    private final Monitor monitor = new Monitor();

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
    public boolean tryProduce(T data, long timeout, TimeUnit timeUnit) {
        try {
            return queue.offer(data, timeout, timeUnit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void produce(T data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("all")
    @Override
    public boolean tryProduce(Collection<T> data, long timeout, TimeUnit timeUnit) {
        boolean acquired = false;
        try {
            acquired = monitor.enterWhen(new Monitor.Guard(monitor) {
                @Override
                public boolean isSatisfied() {
                    return queue.size() + data.size() <= capacity;
                }
            }, timeout, timeUnit);
            if (acquired) {
                for (T t : data) {
                    queue.put(t);
                }
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (acquired) {
                monitor.leave();
            }
        }
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
    public T tryConsume(long timeout, TimeUnit timeUnit) {
        try {
            return queue.poll(timeout, timeUnit);
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
