package org.loguno.processor.configuration;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationImpl implements Configuration {

    private static final String internal = "loguno_internal.properties";

    private static final String external = "loguno.properties";

    private String rootPath;


    // priority 3
    private Properties properties = new Properties();

    private Map<String, Properties> propertiesFromPackages = new HashMap<>();


    public ConfigurationImpl() {

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(internal)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

		/*try (InputStream input1 = Files.newInputStream(Paths.get(sourcepath.replace(";", "") + File.separator + external))) {
			propertiesFromClientSources.load(input1);
		} catch (IOException e) {
			// not critical
		}

		try (InputStream input2 = Files.newInputStream(Paths.get(userdir + File.separator + external))) {
			propertiesFromClientRoot.load(input2);
		} catch (IOException e) {
			// not critical
		}*/

    }

    private void loadPropertyAndPutItToMap(String path) {

        try (InputStream input = Files.newInputStream(Paths.get(path + File.separator + external))) {
            Properties properties = new Properties();
            properties.load(input);

            if (properties.size() == 0)
                return;

            this.rootPath = path;

            propertiesFromPackages.putIfAbsent(path, properties);
        } catch (IOException e) {
            // not critical
        }

    }

    @Override
    public <T> T getProperty(ConfigurationKey<T> key) {

        if (this.rootPath!=null && propertiesFromPackages.getOrDefault(this.rootPath, new Properties()).containsKey(key.getName())) {
            return key.getValue(propertiesFromPackages.get(this.rootPath).getProperty(key.getName()));
        }

        return key.getValue(properties.getProperty(key.getName()));
    }

    @Override
    public <T> T getProperty(ConfigurationKey<T> key, String rootPath) {

        loadPropertyAndPutItToMap(rootPath);

        if (propertiesFromPackages.getOrDefault(rootPath, new Properties()).containsKey(key.getName())) {
            return key.getValue(propertiesFromPackages.get(rootPath).getProperty(key.getName()));
        }

        return key.getValue(properties.getProperty(key.getName()));
    }
}
