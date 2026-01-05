package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {

	private static Properties properties;
	private static final Logger logger = LogManager.getLogger(ConfigReader.class);
	private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";

	static {
		try {
			logger.info("Loading configuration from: " + CONFIG_FILE_PATH);
			FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE_PATH);
			properties = new Properties();
			properties.load(fileInputStream);
			fileInputStream.close();
			logger.info("Configuration loaded successfully");
		} catch (IOException e) {
			logger.error("Failed to load configuration file: " + e.getMessage(), e);
			throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE_PATH, e);
		}
	}

	public static String getProperty(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			logger.warn("Property key '" + key + "' not found in configuration");
		} else {
			logger.debug("Retrieved property: " + key);
		}
		return value;
	}

	public static String getProperty(String key, String defaultValue) {
		String value = properties.getProperty(key, defaultValue);
		logger.debug("Retrieved property: " + key + " with value: " + value);
		return value;
	}
}