package com.aihuishou.pipeline.redisson.constant;

public interface RedissonKey {

    String PIPELINE_REDISSON = "pipeline-redisson:";

    String REDISSON_DATA_BUFFER = PIPELINE_REDISSON + "data-buffer:";

    String REDISSON_TASK_REGISTRY = PIPELINE_REDISSON + "task-registry:";

}
