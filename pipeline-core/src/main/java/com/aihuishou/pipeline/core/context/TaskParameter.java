package com.aihuishou.pipeline.core.context;

/**
 * 任务参数
 * @author ethan zhang
 */
public interface TaskParameter {

    TaskParameter addParameter(String key, Object value);

    String getString(String key);

    <T> T getObject(String key, Class<T> clazz);

}
