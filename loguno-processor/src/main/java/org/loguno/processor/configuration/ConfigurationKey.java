package org.loguno.processor.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class ConfigurationKey<T> {

    @Getter(AccessLevel.PACKAGE)
    private final String name;
    @Getter(AccessLevel.PACKAGE)
    private final String description;

    public T getValue(String value){
        return (T)value;
    }
}
