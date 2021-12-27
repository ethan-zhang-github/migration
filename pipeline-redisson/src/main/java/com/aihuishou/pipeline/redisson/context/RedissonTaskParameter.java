package com.aihuishou.pipeline.redisson.context;

import com.aihuishou.pipeline.core.context.TaskParameter;
import com.aihuishou.pipeline.redisson.constant.RedissonKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;

public class RedissonTaskParameter implements TaskParameter {

    private final RMap<String, Object> parameters;

    public RedissonTaskParameter(RedissonClient redissonClient, String id) {
        this.parameters = redissonClient.getMap(RedissonKey.REDISSON_TASK_PARAMETER + id);
    }

    @Override
    public RedissonTaskParameter addParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    @Override
    public TaskParameter addParameters(Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    @Override
    public String getString(String key) {
        return getObject(key, String.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        Object val = parameters.get(key);
        if (val != null) {
            if (clazz.isAssignableFrom(val.getClass())) {
                return (T) val;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> asMap() {
        return new HashMap<>(parameters);
    }

}
