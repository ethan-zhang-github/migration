package com.aihuishou.pipeline.redisson.constant;

public interface RedissonKey {

    String PIPELINE_REDISSON = "pipeline-redisson:";

    String REDISSON_DATA_BUFFER = PIPELINE_REDISSON + "data-buffer:";

    String REDISSON_TASK_REGISTRY = PIPELINE_REDISSON + "task-registry:";

    String REDISSON_TASK_PARAMETER = PIPELINE_REDISSON + "task-parameter:";

    String REDISSON_COUNTER = PIPELINE_REDISSON + "counter:";

    String REDISSON_MARKER = PIPELINE_REDISSON + "marker:";

    String REDISSON_TASK_STATE = PIPELINE_REDISSON + "task-state:";

    String REDISSON_HOLDER = PIPELINE_REDISSON + "holder:";

    String REDISSON_TASK_ID_GENERATOR = PIPELINE_REDISSON + "task-id-generator:";

    String REDISSON_TASK_EVENT_DISPATCHER = PIPELINE_REDISSON + "task-event-dispatcher";

}
