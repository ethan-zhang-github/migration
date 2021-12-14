package priv.ethanzhang.pipeline.core.utils;

public class ThreadUtil {

    public static boolean isCurThreadInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

}
