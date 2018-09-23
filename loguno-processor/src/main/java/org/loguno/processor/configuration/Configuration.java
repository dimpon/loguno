package org.loguno.processor.configuration;

public interface Configuration {
    <T> T getProperty(ConfigurationKey<T> key);
}
