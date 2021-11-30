package priv.ethanzhang.migration.core.context;

/**
 * 任务状态
 */
public enum MigrationState {

    /**
     * 创建未运行
     */
    NEW {
        @Override
        public boolean isFinalState() {
            return false;
        }
    },
    /**
     * 运行中
     */
    RUNNING {
        @Override
        public boolean isFinalState() {
            return false;
        }
    },
    /**
     * 暂停
     */
    STOPPING {
        @Override
        public boolean isFinalState() {
            return false;
        }
    },
    /**
     * 终止
     */
    TERMINATED {
        @Override
        public boolean isFinalState() {
            return true;
        }
    },
    /**
     * 失败
     */
    FAILED {
        @Override
        public boolean isFinalState() {
            return true;
        }
    };

    public abstract boolean isFinalState();

}
