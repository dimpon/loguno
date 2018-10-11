package org.loguno.processor.configuration;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.InputStream;

import java.util.Properties;

public class ConfigurationImpl implements Configuration {
    private static final String propFileName= "loguno_internal.properties";

    private Properties properties = new Properties();

    @SneakyThrows
    public ConfigurationImpl() {

        @Cleanup
        InputStream input = getClass().getClassLoader().getResourceAsStream(propFileName);
        properties.load(input);
    }

    @Override
    public <T> T getProperty(ConfigurationKey<T> key) {
        return key.getValue(properties.getProperty(key.getName()));
    }
}
