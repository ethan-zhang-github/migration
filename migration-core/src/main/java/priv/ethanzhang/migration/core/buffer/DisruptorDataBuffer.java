package priv.ethanzhang.migration.core.buffer;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import priv.ethanzhang.migration.core.exception.MigrationTaskBuildException;
import priv.ethanzhang.migration.core.utils.GenericUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DisruptorDataBuffer<T> implements DataBuffer<T> {

    private final Disruptor<T> disruptor;

    public DisruptorDataBuffer(int capacity) {
        disruptor = new Disruptor<T>(getEventFactory(), capacity, DaemonThreadFactory.INSTANCE);
    }

    @Override
    public boolean isEmpty() {
        return disruptor.getRingBuffer().remainingCapacity() == disruptor.getBufferSize();
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
        return (int) disruptor.getBufferSize();
    }

    @Override
    public boolean tryProduce(T data, long timeout, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public void produce(T data) {

    }

    @Override
    public boolean tryProduce(Collection<T> data, long timeout, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public T consume() {
        return null;
    }

    @Override
    public T tryConsume(long timeout, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public List<T> consumeIfPossible(int maxsize) {
        return null;
    }

    private EventFactory<T> getEventFactory() {
        Class<T> genericType = GenericUtil.getSuperclassGenericType(this.getClass(), 0);
        try {
            Constructor<T> constructor = genericType.getConstructor();
            return () -> {
                try {
                    return constructor.newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new MigrationTaskBuildException("", e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new MigrationTaskBuildException("", e);
        }
    }

}
