package priv.ethanzhang.pipeline.core.processor;

import lombok.Getter;
import lombok.Setter;
import priv.ethanzhang.pipeline.core.buffer.DataBuffer;
import priv.ethanzhang.pipeline.core.config.GlobalConfig;
import priv.ethanzhang.pipeline.core.context.TaskState;
import priv.ethanzhang.pipeline.core.context.TaskStateHolder;

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
    private TaskStateHolder state;

    /**
     * 写出缓冲区
     */
    private DataBuffer<O> buffer;

    @SuppressWarnings("unchecked")
    public PipeProcessorNode(PipeProcessor<I, O> processor, int bufferSize) {
        this.processor = processor;
        this.state = new TaskStateHolder();
        this.buffer = GlobalConfig.BUFFER.getDefaultDataBuffer().apply(bufferSize);
    }

    public TaskState getState() {
        return state.get();
    }

    public void setState(TaskState target) {
        state.transfer(target);
    }

}
