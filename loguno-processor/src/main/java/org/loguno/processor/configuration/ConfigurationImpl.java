package org.loguno.processor.configuration;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationImpl implements Configuration {

	private static final String internal = "loguno_internal.properties";

	private static final String external = "loguno.properties";

	public static String sourcepath;
	public static String userdir;

	// priority 3
	private Properties properties = new Properties();

	// priority 1
	private Properties propertiesFromClientSources = new Properties();

	// priority 2
	private Properties propertiesFromClientRoot = new Properties();

	public ConfigurationImpl() {

		try (InputStream input = getClass().getClassLoader().getResourceAsStream(internal)) {
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try (InputStream input1 = Files.newInputStream(Paths.get(sourcepath.replace(";", "") + File.separator + external))) {
			propertiesFromClientSources.load(input1);
		} catch (IOException e) {
			// not critical
		}

		try (InputStream input2 = Files.newInputStream(Paths.get(userdir + File.separator + external))) {
			propertiesFromClientRoot.load(input2);
		} catch (IOException e) {
			// not critical
		}

	}

	@Override
	public <T> T getProperty(ConfigurationKey<T> key) {

		if (propertiesFromClientSources.containsKey(key.getName())) {
			return key.getValue(propertiesFromClientSources.getProperty(key.getName()));
		}

		if (propertiesFromClientRoot.containsKey(key.getName())) {
			return key.getValue(propertiesFromClientRoot.getProperty(key.getName()));
		}

		return key.getValue(properties.getProperty(key.getName()));
	}
}
