package com.aihuishou.pipeline.core.utils;

import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    public static boolean isCurThreadInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    public static void sleep(long duration, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(duration));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void interrupt() {
        Thread.currentThread().interrupt();
    }

}
