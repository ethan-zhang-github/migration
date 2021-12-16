package com.aihuishou.pipeline.core.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 任务参数
 */
public class TaskParameter {

    private final JSONObject parameters;

    private TaskParameter(JSONObject parameters) {
        this.parameters = parameters;
    }

    public static TaskParameter newInstance() {
        return new TaskParameter(new JSONObject());
    }

    public static TaskParameter fromJson(String json) {
        return new TaskParameter(JSON.parseObject(json));
    }

    public TaskParameter addParameter(String key, Object value) {
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
