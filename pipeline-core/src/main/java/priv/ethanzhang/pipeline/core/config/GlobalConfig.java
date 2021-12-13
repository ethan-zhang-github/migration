package priv.ethanzhang.pipeline.core.config;

import lombok.Getter;
import lombok.Setter;

public interface GlobalConfig {

    LocalRegistry LOCAL_REGISTRY = new LocalRegistry();

    Reader READER = new Reader();

    Processor PROCESSOR = new Processor();

    Writer WRITER = new Writer();

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
    class LocalRegistry {

        private LocalRegistry() {}

        private int initialCapacity = 16;

        private int maximumSize = Integer.MAX_VALUE;

        private long expireSeconds = Long.MAX_VALUE;

    }

}
