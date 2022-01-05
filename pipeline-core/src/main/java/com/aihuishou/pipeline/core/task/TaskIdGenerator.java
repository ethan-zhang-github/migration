package com.aihuishou.pipeline.core.task;

/**
 * task id 生成器
 * @author ethan zhang
 */
@FunctionalInterface
public interface TaskIdGenerator {

    String generate();

}
