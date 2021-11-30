package priv.ethanzhang.migration.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MigrationConfig {

    Class<? extends Throwable>[] interruptFor() default {};

    int maxProduceWaitSeconds() default 5;

    int maxProduceRetryTimes() default 5;

    int maxConsumeCount() default 100;

}
