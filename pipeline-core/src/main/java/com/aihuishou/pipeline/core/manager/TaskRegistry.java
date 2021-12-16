package com.aihuishou.pipeline.core.manager;

import com.aihuishou.pipeline.core.task.PipeTask;

import java.util.Map;

/**
 * 任务注册表
 */
public interface TaskRegistry {

    void register(PipeTask<?, ?> task);

    void unregister(PipeTask<?, ?> task);

    Map<String, PipeTask<?, ?>> getAll();

    void clear();

}
