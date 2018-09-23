package org.loguno.processor.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfiguratorManager {

    private static ConfiguratorManager $instance = new ConfiguratorManager();

    private final Configuration configuration = new ConfigurationImpl();

    public static ConfiguratorManager getInstance() {
        return $instance;
    }
}
