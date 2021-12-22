package com.aihuishou.pipeline.core.context;

import java.util.Map;

/**
 * 任务参数
 * @author ethan zhang
 */
public interface TaskParameter {

    TaskParameter addParameter(String key, Object value);

    TaskParameter addParameters(Map<String, Object> parameters);

    String getString(String key);

    <T> T getObject(String key, Class<T> clazz);

}
