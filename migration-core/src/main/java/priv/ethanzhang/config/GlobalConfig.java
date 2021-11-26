package priv.ethanzhang.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalConfig {

    public static final GlobalConfig INSTANCE = new GlobalConfig();

    private GlobalConfig() {}

    private LocalMigrationTaskManagerConfig localMigrationTaskManagerConfig = new LocalMigrationTaskManagerConfig();

    @Getter
    @Setter
    public static class LocalMigrationTaskManagerConfig {

        private LocalMigrationTaskManagerConfig() {}

        private int initialCapacity = 16;

        private int maximumSize = Integer.MAX_VALUE;

        private long expireSeconds = Long.MAX_VALUE;

    }

}
