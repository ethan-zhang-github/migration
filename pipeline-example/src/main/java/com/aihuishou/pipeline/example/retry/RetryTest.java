package com.aihuishou.pipeline.example.retry;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

@Slf4j
public class RetryTest {

    @Test
    public void test() {
        Retryer<Object> retryer = RetryerBuilder.newBuilder()
                .retryIfResult(Boolean.FALSE::equals)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        try {
            Object call = retryer.call(() -> {
                return true;
            });
            System.out.println(call);
        } catch (ExecutionException e) {
            log.error("执行异常", e);
        } catch (RetryException e) {
            log.error("重试异常", e);
        }
    }

}
