package com.aihuishou.pipeline.core.processor;

import com.aihuishou.pipeline.core.buffer.DataBuffer;
import com.aihuishou.pipeline.core.config.GlobalConfig;
import com.aihuishou.pipeline.core.common.Holder;
import com.aihuishou.pipeline.core.context.LocalTaskStateHolder;
import com.aihuishou.pipeline.core.context.TaskState;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据处理链节点
 * @param <I> 输入类型
 * @param <O> 输出类型
 * @author ethan zhang
 */
@Getter
@Setter
public class PipeProcessorNode<I, O> {

    /**
     * 当前节点的 processor
     */
    private PipeProcessor<I, O> processor;

    /**
     * 当前节点状态
     */
    private Holder<TaskState> state;

    /**
     * 写出缓冲区
     */
    private DataBuffer<O> buffer;

    @SuppressWarnings("unchecked")
    public PipeProcessorNode(PipeProcessor<I, O> processor, int bufferSize) {
        this.processor = processor;
        this.state = new LocalTaskStateHolder();
        this.buffer = GlobalConfig.BUFFER.getDefaultDataBuffer().apply(bufferSize);
    }

    public PipeProcessorNode(PipeProcessor<I, O> processor) {
        this.processor = processor;
        this.state = new LocalTaskStateHolder();
        this.buffer = DataBuffer.empty();
    }

    public Holder<TaskState> getState() {
        return state;
    }

}
