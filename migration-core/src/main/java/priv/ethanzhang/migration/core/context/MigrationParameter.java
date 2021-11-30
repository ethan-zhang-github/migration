package priv.ethanzhang.migration.core.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 任务参数
 */
public class MigrationParameter {

    private final JSONObject parameters;

    private MigrationParameter(JSONObject parameters) {
        this.parameters = parameters;
    }

    public static MigrationParameter newInstance() {
        return new MigrationParameter(new JSONObject());
    }

    public static MigrationParameter fromJson(String json) {
        return new MigrationParameter(JSON.parseObject(json));
    }

    public MigrationParameter addParameter(String key, Object value) {
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
