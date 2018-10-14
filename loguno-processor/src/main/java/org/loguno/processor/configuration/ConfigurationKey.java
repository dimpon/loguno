package org.loguno.processor.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
public class ConfigurationKey<T> {

    @Getter(AccessLevel.PACKAGE)
    private final String name;
    @Getter(AccessLevel.PACKAGE)
    private final String description;

    private Function<String, T> transform = (e) -> (T) e;

    public T getValue(String value) {
        return transform.apply(value);
    }
}
