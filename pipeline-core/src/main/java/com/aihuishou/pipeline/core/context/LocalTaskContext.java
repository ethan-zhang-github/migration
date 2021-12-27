package com.aihuishou.pipeline.core.context;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.common.LocalCounter;
import com.aihuishou.pipeline.core.common.LocalHolder;
import com.aihuishou.pipeline.core.common.LocalOnceHolder;
import com.aihuishou.pipeline.core.task.PipeTask;

/**
 * 本地任务上下文
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
public class LocalTaskContext<I, O> extends AbstractTaskContext<I, O> {

    public static <I, O> Builder<I, O> builder() {
        return new Builder<>();
    }

    public static class Builder<I, O> {

        private final LocalTaskContext<I, O> context = new LocalTaskContext<>();

        public Builder<I, O> task(PipeTask<I, O> task) {
            context.setTask(task);
            return Builder.this;
        }

        public Builder<I, O> parameter(TaskParameter parameter) {
            context.setParameter(parameter);
            return Builder.this;
        }

        public Builder<I, O> readBuffer(DataBuffer<I> readerBuffer) {
            context.setReadBuffer(readerBuffer);
            return Builder.this;
        }

        public Builder<I, O> writeBuffer(DataBuffer<O> writerBuffer) {
            context.setWriteBuffer(writerBuffer);
            return Builder.this;
        }

        public LocalTaskContext<I, O> build() {

            return context;
        }

    }

}
