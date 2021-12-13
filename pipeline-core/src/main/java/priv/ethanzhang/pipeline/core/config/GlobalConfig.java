package priv.ethanzhang.pipeline.core.config;

import lombok.Getter;
import lombok.Setter;
import priv.ethanzhang.pipeline.core.buffer.DataBuffer;
import priv.ethanzhang.pipeline.core.buffer.DisruptorDataBuffer;
import priv.ethanzhang.pipeline.core.event.dispatcher.DisruptorTaskEventDispatcher;
import priv.ethanzhang.pipeline.core.event.dispatcher.TaskEventDispatcher;
import priv.ethanzhang.pipeline.core.reporter.LoggerTaskReporter;
import priv.ethanzhang.pipeline.core.reporter.TaskReporter;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 全局配置
 * @author ethan zhang
 */
public interface GlobalConfig {

    LocalRegistry LOCAL_REGISTRY = new LocalRegistry();

    LocalDispatcher LOCAL_DISPATCHER = new LocalDispatcher();

    Reader READER = new Reader();

    Processor PROCESSOR = new Processor();

    Writer WRITER = new Writer();

    Buffer BUFFER = new Buffer();

    Reporter REPORTER = new Reporter();

    @Getter
    @Setter
    class Reader {

        private Reader() {}

        private int produceRetryTimes = 10;

        private int produceWaitSeconds = 5;

    }

    @Getter
    @Setter
    class Processor {

        private Processor() {}

        private int produceRetryTimes = 10;

        private int produceWaitSeconds = 5;

        private int maxConsumeCount = 100;

    }

    @Getter
    @Setter
    class Writer {

        private Writer() {}

        private int maxConsumeCount = 100;

    }

    @Getter
    @Setter
    class Buffer {

        private int bufferSize = 1 << 10;

        @SuppressWarnings("rawtypes")
        private Function<Integer, DataBuffer> defaultDataBuffer = DisruptorDataBuffer::new;

    }

    @Getter
    @Setter
    class Reporter {

        private Duration reportPeriod = Duration.ofMinutes(1);

        private Supplier<TaskReporter> defaultReporter = () -> LoggerTaskReporter.INSTANCE;

    }

    @Getter
    @Setter
    class LocalRegistry {

        private LocalRegistry() {}

        /**
         * 任务注册表初始大小
         */
        private int initialCapacity = 16;

        /**
         * 任务注册表最大容量（超过则触发任务淘汰）
         */
        private int maximumSize = Integer.MAX_VALUE;

        /**
         * 任务最大过期时间
         */
        private long expireSeconds = Long.MAX_VALUE;

    }

    @Getter
    @Setter
    class LocalDispatcher {

        private int bufferSize = 1 << 10;

        private Supplier<TaskEventDispatcher> defaultDispatcher = () -> DisruptorTaskEventDispatcher.INSTANCE;

    }

}
