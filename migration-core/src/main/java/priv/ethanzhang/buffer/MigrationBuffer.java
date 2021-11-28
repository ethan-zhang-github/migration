package priv.ethanzhang.buffer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 数据缓冲区
 * @param <T> 数据类型
 */
public interface MigrationBuffer<T> {

    boolean isEmpty();

    boolean isFull();

    int size();

    boolean tryProduce(T data, long timeout, TimeUnit timeUnit);

    void produce(T data);

    boolean tryProduce(Collection<T> data, long timeout, TimeUnit timeUnit);

    T consume();

    T tryConsume(long timeout, TimeUnit timeUnit);

    List<T> consumeIfPossible(int maxsize);

}
