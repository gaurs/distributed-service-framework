package com.optimumalgorithms.framework.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This method is used to read the properties file defined and will return the
 * corresponding values as read.
 * 
 * @author gaurs
 * 
 */
public class ConfigReader {
	private static final ConfigReader INSTANCE = new ConfigReader();
	private String pathToPropertiesFile = null;
	private String defaultPath = "";
	private boolean isPathSet = false;

	private ConfigReader() {
		init();
	}

	public static ConfigReader getInstance() {
		return INSTANCE;
	}

	private void init() {
		if (null == pathToPropertiesFile) {
			pathToPropertiesFile = defaultPath;
		}
	}

	/**
	 * This method is used to read the properties file and will return the value
	 * as read from it in String format.
	 * 
	 * @param propertyName
	 * @return
	 * @throws IOException
	 */
	public String getStringValue(String propertyName) throws IOException {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream inputStream = classLoader
				.getResourceAsStream(pathToPropertiesFile + "config.properties");

		if (null == inputStream) {
			inputStream = new FileInputStream(pathToPropertiesFile + "config.properties");
			
			if (null == inputStream) {
				throw new FileNotFoundException(
						"Mapping file is either not defined or not found. Kindly define a mapping file");
			}
		}

		Properties properties = new Properties();
		properties.load(inputStream);

		return properties.getProperty(propertyName);

	}

	public String getPathToPropertiesFile() {
		return pathToPropertiesFile;
	}

	public void setPathToPropertiesFile(String pathToMappingFile) {
		this.pathToPropertiesFile = pathToMappingFile;
		this.isPathSet = true;
	}

	public boolean isPathSet() {
		return isPathSet;
	}

}
