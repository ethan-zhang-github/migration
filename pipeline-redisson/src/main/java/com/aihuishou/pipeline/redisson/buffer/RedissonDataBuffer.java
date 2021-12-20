package com.aihuishou.pipeline.redisson.buffer;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.utils.ThreadUtil;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 数据缓冲区（基于 redisson 有界阻塞式队列实现）
 * @param <T> 数据类型
 * @author ethan zhang
 */
public class RedissonDataBuffer<T> implements DataBuffer<T> {

    private final RBoundedBlockingQueue<T> queue;

    public RedissonDataBuffer(RedissonClient redissonClient, int capacity) {
        this.queue = redissonClient.getBoundedBlockingQueue(RedissonKey.REDISSON_DATA_BUFFER + UUID.randomUUID());
        queue.trySetCapacity(capacity);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean isFull() {
        return queue.remainingCapacity() == 0;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public int capacity() {
        return queue.size() + queue.remainingCapacity();
    }

    @Override
    public void produce(T data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            ThreadUtil.interrupt();
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
            ThreadUtil.interrupt();
            return null;
        }
    }

    @Override
    public List<T> consumeIfPossible(int maxElements) {
        T head = queue.poll();
        if (head == null) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(maxElements);
        list.add(head);
        queue.drainTo(list, maxElements - 1);
        return list;
    }

}
