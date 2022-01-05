package com.aihuishou.pipeline.core.context;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务参数
 */
public class LocalTaskParameter implements TaskParameter {

    private final JSONObject parameters;

    public LocalTaskParameter() {
        this.parameters = new JSONObject();
    }

    @Override
    public LocalTaskParameter addParameter(String key, Object value) {
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
        return parameters.getString(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return parameters.getObject(key, clazz);
    }

    @Override
    public Map<String, Object> asMap() {
        return new HashMap<>(parameters);
    }

    @Override
    public String toString() {
        return parameters.toString();
    }

}
