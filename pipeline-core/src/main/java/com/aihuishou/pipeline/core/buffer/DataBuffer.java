package com.aihuishou.pipeline.core.buffer;

import java.util.List;

/**
 * 数据缓冲区
 * @param <T> 数据类型
 */
public interface DataBuffer<T> {

    @SuppressWarnings("rawtypes")
    DataBuffer EMPTY_BUFFER = new EmptyDataBuffer<>();

    @SuppressWarnings("unchecked")
    static <E> DataBuffer<E> empty() {
        return (DataBuffer<E>) EMPTY_BUFFER;
    }

    /**
     * 缓冲区是否为空
     */
    boolean isEmpty();

    /**
     * 缓冲区是否已满
     */
    boolean isFull();

    /**
     * 缓冲区当前元素数量
     */
    int size();

    /**
     * 缓冲区容量
     */
    int capacity();

    /**
     * 生产元素，若缓冲区已满，阻塞直到有空闲位置
     */
    void produce(T data);

    /**
     * 尝试生产元素，若缓冲区已满，则生产失败，返回 false
     */
    boolean tryProduce(T data);

    /**
     * 消费元素，若缓冲区为空，阻塞直到有元素可消费
     */
    T consume();

    /**
     * 尝试消费固定数量的元素，若缓冲区的元素数量不足则消费所有可消费的元素
     */
    List<T> consumeIfPossible(int maxElements);

}
