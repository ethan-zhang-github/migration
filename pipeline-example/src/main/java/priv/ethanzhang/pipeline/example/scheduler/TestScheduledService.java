package priv.ethanzhang.pipeline.example.scheduler;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestScheduledService extends AbstractScheduledService {

    @Override
    protected void runOneIteration() throws Exception {
        System.out.println(1);
    }

    @SuppressWarnings("all")
    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(Duration.ofSeconds(1), Duration.ofSeconds(1));
    }

    @Override
    protected void shutDown() throws Exception {
        System.out.println(2);
    }

    @Test
    public void test() throws InterruptedException {
        TestScheduledService service = new TestScheduledService();
        service.startAsync();
        new CountDownLatch(1).await(10, TimeUnit.SECONDS);
        service.stopAsync();
        new CountDownLatch(1).await(10, TimeUnit.SECONDS);
    }

}
