package com.aihuishou.pipeline.core.event;

import lombok.Getter;

import java.time.Instant;

/**
 * 任务事件
 * @author ethan zhang
 */
@Getter
public abstract class TaskEvent {

    protected final Instant timestamp = Instant.now();

}
