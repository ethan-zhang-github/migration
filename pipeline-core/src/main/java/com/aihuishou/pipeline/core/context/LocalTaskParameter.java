package com.aihuishou.pipeline.core.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 任务参数
 */
public class LocalTaskParameter {

    private final JSONObject parameters;

    private LocalTaskParameter(JSONObject parameters) {
        this.parameters = parameters;
    }

    public static LocalTaskParameter newInstance() {
        return new LocalTaskParameter(new JSONObject());
    }

    public static LocalTaskParameter fromJson(String json) {
        return new LocalTaskParameter(JSON.parseObject(json));
    }

    public LocalTaskParameter addParameter(String key, Object value) {
        parameters.put(key, value);
        return this;
    }

    public String getString(String key) {
        return parameters.getString(key);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        return parameters.getObject(key, clazz);
    }

    @Override
    public String toString() {
        return parameters.toString();
    }

}
