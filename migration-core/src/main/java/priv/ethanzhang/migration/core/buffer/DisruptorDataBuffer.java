package priv.ethanzhang.migration.core.buffer;

import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.sun.xml.internal.fastinfoset.util.ValueArray.MAXIMUM_CAPACITY;

/**
 * 数据缓冲区（基于 Disruptor 无锁队列实现）
 * @param <T> 数据类型
 */
public class DisruptorDataBuffer<T> implements DataBuffer<T> {

    private final Disruptor<EventWrapper<T>> disruptor;

    private final ConcurrentLinkedQueue<T> buffer;

    private final int actualCapacity;

    public DisruptorDataBuffer(int capacity) {
        actualCapacity = capacityFor(capacity);
        disruptor = new Disruptor<>(EventWrapper::new, actualCapacity, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(this::onEvent);
        buffer = new ConcurrentLinkedQueue<>();
        disruptor.start();
    }

    @Override
    public boolean isEmpty() {
        return (disruptor.getRingBuffer().remainingCapacity() == disruptor.getBufferSize()) && buffer.isEmpty();
    }

    @Override
    public boolean isFull() {
        return disruptor.getRingBuffer().remainingCapacity() == 0;
    }

    @Override
    public int size() {
        return (int) (disruptor.getBufferSize() - disruptor.getRingBuffer().remainingCapacity());
    }

    @Override
    public int capacity() {
        return actualCapacity;
    }

    @Override
    public void produce(T data) {
        disruptor.getRingBuffer().publishEvent(this::translate, data);
    }

    @Override
    public boolean tryProduce(T data) {
        return disruptor.getRingBuffer().tryPublishEvent(this::translate, data);
    }

    @Override
    public T consume() {
        while (true) {
            T head = buffer.poll();
            if (head != null) {
                return head;
            } else {
                Thread.yield();
            }
        }
    }

    @Override
    public List<T> consumeIfPossible(int maxsize) {
        List<T> list = new ArrayList<>(maxsize);
        while (true) {
            if (list.size() >= maxsize) {
                return list;
            }
            T head = buffer.poll();
            if (head == null) {
                return list;
            }
            list.add(head);
        }
    }

    private void translate(EventWrapper<T> event, long sequence, T data) {
        event.setData(data);
    }

    private void onEvent(EventWrapper<T> event, long sequence, boolean endOfBatch) {
        while (true) {
            if (buffer.size() < actualCapacity) {
                buffer.offer(event.getData());
                return;
            } else {
                Thread.yield();
            }
        }
    }

    private int capacityFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    @Getter
    @Setter
    private static class EventWrapper<T> {

        private T data;

    }

}
