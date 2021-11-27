package priv.ethanzhang.buffer;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LocalMigrationBuffer<T> implements MigrationBuffer<T> {

    private final int capacity;

    private final BlockingQueue<T> queue;

    public LocalMigrationBuffer(int capacity) {
        this.capacity = capacity;
        this.queue = new ArrayBlockingQueue<>(capacity);
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
    public void publish(T data) {
        Thread currentThread = Thread.currentThread();
        try {
            while (!currentThread.isInterrupted()) {
                if (queue.offer(data, 1, TimeUnit.MINUTES)) {
                    return;
                } else {
                    // TODO publish
                    Thread.yield();
                }
            }
        } catch (InterruptedException e) {
            currentThread.interrupt();
        }
    }

    @Override
    public void publishBatch(Collection<T> data) {
        data.forEach(this::publish);
    }

    @Override
    public T subscribe() {
        Thread currentThread = Thread.currentThread();
        try {
            while (!currentThread.isInterrupted()) {
                T poll = queue.poll(1, TimeUnit.MINUTES);
                if (poll != null) {
                    return poll;
                } else {
                    // TODO publish
                    Thread.yield();
                }
            }
        } catch (InterruptedException e) {
            currentThread.interrupt();
        }
        return null;
    }

    @Override
    public List<T> subscribeBatch(int size) {
        List<T> list = new ArrayList<>(queue.size());
        T head = subscribe();
        if (head == null) {
            return Collections.emptyList();
        }
        list.add(head);
        queue.drainTo(list, size);
        return list;
    }

    @Override
    public List<T> subscribeAll() {
        List<T> list = new ArrayList<>(queue.size());
        T head = subscribe();
        if (head == null) {
            return Collections.emptyList();
        }
        list.add(head);
        queue.drainTo(list);
        return list;
    }

}
