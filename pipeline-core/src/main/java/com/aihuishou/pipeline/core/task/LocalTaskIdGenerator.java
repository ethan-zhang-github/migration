package com.aihuishou.pipeline.core.task;

import java.util.UUID;

public enum LocalTaskIdGenerator implements TaskIdGenerator {

    INSTANCE;

    private static final String PREFIX = "LOCAL-TASK-";

    @Override
    public String generate() {
        return PREFIX + UUID.randomUUID();
    }

}
