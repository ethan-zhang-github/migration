package priv.ethanzhang.pipeline.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TaskConfig {

    Class<? extends Throwable>[] interruptFor() default { Throwable.class };

    Class<? extends Throwable>[] ignoreFor() default {};

    int produceRetryPeriodSeconds() default 5;

    int maxProduceRetryTimes() default 10;

    int maxConsumeCount() default 100;

}
