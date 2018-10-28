package org.loguno.processor.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationImpl implements Configuration {

    private static final String internal = "loguno_internal.properties";

    private static final String external = "loguno.properties";

    private static final String DEFAULT = "DEFAULT";

    private static final String ROOT = "ROOT";

    private static final String STOP_SYM = "src";

    private static final Properties EMPTY = new Properties();

    private Map<String, Properties> properties = new HashMap<>();


    public ConfigurationImpl() {
        loadDefaultProperties();
    }

    private void loadDefaultProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(internal)) {
            Properties prop = new Properties();
            prop.load(input);
            this.properties.putIfAbsent(DEFAULT, prop);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPropertyAndPutItToMap(String path) {
        loadPropertyAndPutItToMap(path, path);
    }

    private void loadPropertyAndPutItToMap(String path, String key) {

        if (properties.containsKey(key))
            return;

        try (InputStream input = Files.newInputStream(Paths.get(path + File.separator + external))) {
            final Properties prop = new Properties();
            prop.load(input);

            properties.computeIfAbsent(key, s -> (prop.size() == 0) ? EMPTY : prop);
        } catch (IOException e) {
            properties.putIfAbsent(key, EMPTY);
        }
    }

    @Override
    public <T> T getProperty(ConfigurationKey<T> key) {

        final String fi = ThreadLocalHolder.get();
        if (fi != null) {
            scanPackagesTillStopSym(fi);

            Optional<String> first = properties.keySet().stream()
                    .filter(fi::contains)
                    .sorted(Comparator.comparingInt(String::length).reversed())
                    .map(s -> properties.get(s).getProperty(key.getName()))
                    .filter(Objects::nonNull)
                    .findFirst();

            if (first.isPresent())
                return key.getValue(first.get());

        }

        T var = getPropFromOneMap(key, ROOT);
        if (var != null)
            return var;

        return getPropFromOneMap(key, DEFAULT);
    }

    private <T> T getPropFromOneMap(ConfigurationKey<T> key, String path) {
        String v = properties.getOrDefault(path, EMPTY).getProperty(key.getName());
        if (v == null)
            return null;
        return key.getValue(v);
    }

    @Override
    public <T> T getProperty(ConfigurationKey<T> key, String rootPath) {
        loadPropertyAndPutItToMap(rootPath, ROOT);
        return getProperty(key);
    }

    private void scanPackagesTillStopSym(String pa) {
        while (!pa.endsWith(STOP_SYM)) {
            pa = pa.substring(0, pa.lastIndexOf(File.separator));
            if(properties.containsKey(pa))
                break;
            loadPropertyAndPutItToMap(pa);
        }
    }
}
