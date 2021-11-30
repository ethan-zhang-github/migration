package priv.ethanzhang.migration.core.utils;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class GenericUtil {

    public static Class<?> getInterfaceGenericType(Class<?> targetType, Class<?> interfaceType, int offset) {
        return (Class<?>) Arrays.stream(targetType.getGenericInterfaces()).filter(t -> t instanceof ParameterizedType)
                .map(ParameterizedType.class::cast).filter(t -> t.getRawType() == interfaceType)
                .map(t -> t.getActualTypeArguments()[offset]).findFirst().orElse(null);
    }

}
