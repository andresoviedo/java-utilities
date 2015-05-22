package org.andresoviedo.util.messaging.api1.common.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.andresoviedo.util.messaging.api1.MessengerProperties;


/**
 * A basic configuration object.
 * 
 * @author andres
 */
public class BasicConfiguration {

	/**
	 * A static reference to the logger object.
	 */
	private static Logger logger = Logger
			.getLogger(MessengerProperties.LOGGER_NAME);

	/**
	 * The properties map.
	 */
	protected Properties properties;

	/**
	 * Creates a new base configuration.
	 */
	public BasicConfiguration() {
		this.properties = new Properties();
	}

	/**
	 * <p>
	 * Gets the value of the property stored with the specified key. This method
	 * does the following:
	 * </p>
	 * <ol>
	 * <li>If a system property stored with that key is found, returns its
	 * value.</li>
	 * <li>If a system property stored with that key is NOT found, then it looks
	 * inside our own <code>properties</code> object.
	 * <li>If the property is not found in the system properties nor in our
	 * <code>properties</code> object, returns the passed in default value.
	 * </ol>
	 * 
	 * @param key
	 *            the property key.
	 * @param def
	 *            the default value.
	 * 
	 * @return the value of the property stored with the specified key.
	 */
	protected String getProperty(String key, String def) {
		return getProperty(key, key, def);
	}

	/**
	 * Performs exactly as <code>getProperty(String, String)</code> but allows
	 * to specify a different property name when looking inside system
	 * properties map.
	 * 
	 * @param key
	 *            the property key.
	 * @param systemKey
	 *            the system property key.
	 * @param def
	 *            the default value.
	 * @return the value of the property stored with the specified key.
	 */
	protected String getProperty(String key, String systemKey, String def) {
		String result = System.getProperty(systemKey);
		if (result == null) {
			// No system property defined.
			result = properties.getProperty(key, def);
		}
		// if (result == null) {
		// System.getProperties().remove(systemKey);
		// } else {
		// System.setProperty(systemKey, result);
		// }
		return result;
	}

	/**
	 * Loads the properties from a file which name is read from a system
	 * property.
	 * 
	 * @param key
	 *            the system property name of the filename.
	 * @param def
	 *            the default filename value.
	 */
	protected void load(String key, String def) {
		String pathname = System.getProperty(key, def);
		if (pathname != null) {
			load(new File(pathname));
		}
	}

	/**
	 * Loads the properties from the specified file.
	 * 
	 * @param file
	 *            the file to load properties from.
	 */
	protected void load(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			properties.load(fis);
			fis.close();
			return;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		logger.warning("File " + file
				+ " not found. Trying to load resource...");
		InputStream resourceAsStream = BasicConfiguration.class
				.getResourceAsStream("/" + file.getName());
		if (resourceAsStream == null) {
			logger.severe("Resource not found: " + file.getName());
			return;
		}

		try {
			properties.load(resourceAsStream);
			logger.info("config loaded: " + properties);
		} catch (IOException ex2) {
			logger.warning("Couldn't load resource. " + ex2.getMessage());
		}
	}
}