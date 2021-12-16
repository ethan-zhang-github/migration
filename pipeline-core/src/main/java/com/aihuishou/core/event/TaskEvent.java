package com.aihuishou.core.event;

import lombok.Getter;

import java.time.Instant;

/**
 * 任务事件
 * @author ethan zhang
 */
@Getter
public abstract class TaskEvent {

    private final Instant timestamp = Instant.now();

}
