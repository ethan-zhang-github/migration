package priv.ethanzhang.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MigrationConfig {

    Class<? extends Throwable>[] shutdownFor() default {};

    int maxProduceWaitSeconds() default 5;

    int maxProduceRetryTimes() default 5;

}
