package com.aihuishou.pipeline.redisson.task;

import com.aihuishou.pipeline.core.context.LocalTaskParameter;
import com.aihuishou.pipeline.core.task.AbstractPipeTaskBuilder;
import com.aihuishou.pipeline.core.task.PipeTask;
import com.aihuishou.pipeline.redisson.common.RedissonBootstrap;
import com.aihuishou.pipeline.redisson.common.RedissonCounter;
import com.aihuishou.pipeline.redisson.common.RedissonHolder;
import com.aihuishou.pipeline.redisson.common.RedissonOnceHolder;
import com.aihuishou.pipeline.redisson.context.RedissonTaskContext;
import com.aihuishou.pipeline.redisson.context.RedissonTaskParameter;
import com.aihuishou.pipeline.redisson.context.RedissonTaskStateHolder;
import org.redisson.api.RedissonClient;

import java.util.Optional;

public class RedissonPipeTaskBuilder<I, O> extends AbstractPipeTaskBuilder<I, O, RedissonPipeTaskBuilder<I, O>> {

    private RedissonClient redissonClient;

    private RedissonPipeTaskBuilder() {
        super(LocalTaskParameter::new, null, null);
    }

    @Override
    protected void initialize(PipeTask<I, O> task) {
        task.setTaskId(Optional.ofNullable(taskId).orElse(RedissonTaskIdGenerator.getInstance().generate()));
        task.setReader(reader);
        task.setProcessorChain(processorChain);
        task.setWriter(writer);
        task.setReporter(reporter);
        task.setDispatcher(null);
        task.setManager(null);
        task.setExecutor(null);
        RedissonTaskContext<I, O> context = new RedissonTaskContext<>();
        RedissonTaskParameter redissonTaskParameter = new RedissonTaskParameter(RedissonBootstrap.getRedissonClient(), task.getTaskId());
        redissonTaskParameter.addParameters(parameter.asMap());
        context.setParameter(redissonTaskParameter);
        context.setReaderCounter(new RedissonCounter(redissonClient, timeout,"reader-counter-" + task.getTaskId()));
        context.setProcessorCounter(new RedissonCounter(redissonClient, timeout, "processor-counter-" + task.getTaskId()));
        context.setWriterCounter(new RedissonCounter(redissonClient, timeout, "writer-counter-" + task.getTaskId()));
        context.setReaderState(new RedissonTaskStateHolder(redissonClient, timeout, "reader-state-" + task.getTaskId()));
        context.setProcessorState(new RedissonTaskStateHolder(redissonClient, timeout, "processor-state-" + task.getTaskId()));
        context.setWriterState(new RedissonTaskStateHolder(redissonClient, timeout, "writer-state-" + task.getTaskId()));
        long totalVal = total > 0 ? total : totalSupplier != null ? totalSupplier.get() : -1L;
        context.setTotal(new RedissonHolder<>(redissonClient, timeout, totalVal, task.getTaskId()));
        context.setStartTime(new RedissonOnceHolder<>(redissonClient, timeout, task.getTaskId()));
        context.setFinishTime(new RedissonOnceHolder<>(redissonClient, timeout, task.getTaskId()));
        context.setReportPeriod(new RedissonHolder<>(redissonClient, timeout, reportPeriod, task.getTaskId()));
        context.setTimeout(new RedissonHolder<>(redissonClient, timeout, timeout, task.getTaskId()));
        task.setContext(context);
    }

}
