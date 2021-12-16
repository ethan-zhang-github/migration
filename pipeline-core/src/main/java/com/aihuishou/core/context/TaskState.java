package com.aihuishou.core.context;

/**
 * 任务状态
 */
public enum TaskState {

    /**
     * 创建未运行
     */
    NEW {
        @Override
        public boolean isFinalState() {
            return false;
        }

        @Override
        public boolean canRun() {
            return true;
        }

        @Override
        public boolean canStop() {
            return false;
        }

        @Override
        public boolean canShutdown() {
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

        @Override
        public boolean canRun() {
            return false;
        }

        @Override
        public boolean canStop() {
            return true;
        }

        @Override
        public boolean canShutdown() {
            return true;
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

        @Override
        public boolean canRun() {
            return true;
        }

        @Override
        public boolean canStop() {
            return false;
        }

        @Override
        public boolean canShutdown() {
            return true;
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

        @Override
        public boolean canRun() {
            return false;
        }

        @Override
        public boolean canStop() {
            return false;
        }

        @Override
        public boolean canShutdown() {
            return false;
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

        @Override
        public boolean canRun() {
            return false;
        }

        @Override
        public boolean canStop() {
            return false;
        }

        @Override
        public boolean canShutdown() {
            return false;
        }
    };

    public abstract boolean isFinalState();

    public abstract boolean canRun();

    public abstract boolean canStop();

    public abstract boolean canShutdown();

}
