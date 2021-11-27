package priv.ethanzhang.config;

import lombok.Getter;
import lombok.Setter;

public interface GlobalConfig {

    LocalRegistry LOCAL_REGISTRY = new LocalRegistry();

    @Getter
    @Setter
    class LocalRegistry {

        private LocalRegistry() {}

        private int initialCapacity = 16;

        private int maximumSize = Integer.MAX_VALUE;

        private long expireSeconds = Long.MAX_VALUE;

    }

}
