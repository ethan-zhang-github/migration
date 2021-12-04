package priv.ethanzhang.migration.example.buffer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class DisruptorTest {

    @Test
    public void test() throws InterruptedException {

        Disruptor<Item> disruptor = new Disruptor<Item>(Item::new, 1 << 10, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith(new EventHandler<Item>() {
            @Override
            public void onEvent(Item event, long sequence, boolean endOfBatch) throws Exception {
                log.info("1111event: {}, sequence: {}, endOfBatch: {}", event, sequence, endOfBatch);
            }
        }, new EventHandler<Item>() {
            @Override
            public void onEvent(Item event, long sequence, boolean endOfBatch) throws Exception {
                log.info("2222event: {}, sequence: {}, endOfBatch: {}", event, sequence, endOfBatch);
            }
        });

        System.out.println(disruptor.getRingBuffer().getBufferSize());
        System.out.println(disruptor.getRingBuffer().remainingCapacity());
        disruptor.start();

        RingBuffer<Item> ringBuffer = disruptor.getRingBuffer();

        for (int i = 0; i < 10; i++) {
            ringBuffer.publishEvent(this::translate, i);


            /*Integer[] args = Stream.iterate(i * 10, j -> j + 1).limit(10).toArray(Integer[]::new);

            ringBuffer.publishEvents(this::translate, args);*/

            new CountDownLatch(1).await(1, TimeUnit.SECONDS);
        }

        new CountDownLatch(1).await(1, TimeUnit.HOURS);

    }

    public void translate(Item item, long sequence, int i) {}

    @Data
    private static class Item {

        private Instant instant = Instant.now();

        private String uuid = UUID.randomUUID().toString();

        @Override
        public String toString() {
            return uuid;
        }

    }

}
