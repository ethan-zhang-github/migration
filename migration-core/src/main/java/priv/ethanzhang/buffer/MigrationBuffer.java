package priv.ethanzhang.buffer;

import java.util.Collection;
import java.util.List;

public interface MigrationBuffer<T> {

    boolean isEmpty();

    boolean isFull();

    int size();

    void publish(T data);

    void publishBatch(Collection<T> data);

    T subscribe();

    List<T> subscribeBatch(int size);

    List<T> subscribeAll();

}
