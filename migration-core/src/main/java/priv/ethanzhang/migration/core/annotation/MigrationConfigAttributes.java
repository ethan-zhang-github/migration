package priv.ethanzhang.migration.core.annotation;

import lombok.Getter;
import lombok.Setter;
import priv.ethanzhang.migration.core.config.GlobalConfig;
import priv.ethanzhang.migration.core.processor.MigrationProcessor;
import priv.ethanzhang.migration.core.reader.MigrationReader;
import priv.ethanzhang.migration.core.writer.MigrationWriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class MigrationConfigAttributes {

    Set<Class<? extends Throwable>> interruptFor;

    Set<Class<? extends Throwable>> ignoreFor;

    private int produceRetryPeriodSeconds;

    private int maxProduceRetryTimes;

    private int maxConsumeCount;

    public static MigrationConfigAttributes fromClass(Class<?> clazz) {
        MigrationConfig annotation = clazz.getAnnotation(MigrationConfig.class);
        MigrationConfigAttributes attributes = new MigrationConfigAttributes();
        if (annotation != null) {
            attributes.setInterruptFor(Arrays.stream(annotation.interruptFor()).collect(Collectors.toSet()));
            attributes.setIgnoreFor(Arrays.stream(annotation.ignoreFor()).collect(Collectors.toSet()));
            attributes.setProduceRetryPeriodSeconds(annotation.produceRetryPeriodSeconds());
            attributes.setMaxProduceRetryTimes(annotation.maxProduceRetryTimes());
            attributes.setMaxConsumeCount(annotation.maxConsumeCount());
        } else {
            attributes.setInterruptFor(Collections.singleton(Throwable.class));
            attributes.setIgnoreFor(Collections.emptySet());
            if (MigrationReader.class.isAssignableFrom(clazz)) {
                attributes.setProduceRetryPeriodSeconds(GlobalConfig.READER.getProduceWaitSeconds());
                attributes.setMaxProduceRetryTimes(GlobalConfig.READER.getProduceRetryTimes());
            }
            if (MigrationProcessor.class.isAssignableFrom(clazz)) {
                attributes.setProduceRetryPeriodSeconds(GlobalConfig.PROCESSOR.getProduceWaitSeconds());
                attributes.setMaxProduceRetryTimes(GlobalConfig.PROCESSOR.getProduceRetryTimes());
                attributes.setMaxConsumeCount(GlobalConfig.PROCESSOR.getMaxConsumeCount());
            }
            if (MigrationWriter.class.isAssignableFrom(clazz)) {
                attributes.setMaxConsumeCount(GlobalConfig.WRITER.getMaxConsumeCount());
            }
        }
        return attributes;
    }

    public boolean shouldInterruptFor(Throwable throwable) {
        if (ignoreFor.stream().anyMatch(t -> t.isAssignableFrom(throwable.getClass()))) {
            return false;
        }
        if (interruptFor.stream().anyMatch(t -> t.isAssignableFrom(throwable.getClass()))) {
            return true;
        }
        return true;
    }

}
