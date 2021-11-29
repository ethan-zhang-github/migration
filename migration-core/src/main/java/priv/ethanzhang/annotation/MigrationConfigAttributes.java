package priv.ethanzhang.annotation;

import lombok.Getter;
import lombok.Setter;
import priv.ethanzhang.config.GlobalConfig;
import priv.ethanzhang.processor.MigrationProcessor;
import priv.ethanzhang.reader.MigrationReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class MigrationConfigAttributes {

    Set<Class<? extends Throwable>> shutDownFor;

    private int maxProduceWaitSeconds;

    private int maxProduceRetryTimes;

    public static MigrationConfigAttributes fromClass(Class<?> clazz) {
        MigrationConfig annotation = clazz.getAnnotation(MigrationConfig.class);
        MigrationConfigAttributes attributes = new MigrationConfigAttributes();
        if (annotation != null) {
            attributes.setShutDownFor(Arrays.stream(annotation.shutdownFor()).collect(Collectors.toSet()));
            attributes.setMaxProduceWaitSeconds(annotation.maxProduceWaitSeconds());
            attributes.setMaxProduceRetryTimes(annotation.maxProduceRetryTimes());
        } else {
            attributes.setShutDownFor(Collections.singleton(Throwable.class));
            if (MigrationReader.class.isAssignableFrom(clazz)) {
                attributes.setMaxProduceWaitSeconds(GlobalConfig.READER.getProduceWaitSeconds());
                attributes.setMaxProduceRetryTimes(GlobalConfig.READER.getProduceRetryTimes());
            }
            if (MigrationProcessor.class.isAssignableFrom(clazz)) {
                attributes.setMaxProduceWaitSeconds(GlobalConfig.PROCESSOR.getProduceWaitSeconds());
                attributes.setMaxProduceRetryTimes(GlobalConfig.PROCESSOR.getProduceRetryTimes());
            }
        }
        return attributes;
    }

}
