package priv.ethanzhang.context;

/**
 * 任务状态
 */
public enum MigrationState {

    /**
     * 创建未运行
     */
    NEW,
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 暂停
     */
    STOPPING,
    /**
     * 终止
     */
    TERMINATED,
    /**
     * 失败
     */
    FAILED

}
