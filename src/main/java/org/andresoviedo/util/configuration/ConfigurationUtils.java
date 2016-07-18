package org.andresoviedo.util.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Bind Properties file to Pojo. A prefix can be used so 1 properties file can be used to bind different Pojos.
 * 
 * @author andresoviedo
 *
 */
public class ConfigurationUtils {

	public static void propertiesToPojo(Object pojo, String propertiesFile) {
		propertiesToPojo(pojo, new File(propertiesFile));
	}

	public static void propertiesToPojo(Object pojo, String propertiesFile, String prefix) {
		propertiesToPojo(pojo, new File(propertiesFile), prefix);
	}

	public static void propertiesToPojo(Object pojo, File propertiesFile) {
		propertiesToPojo(pojo, propertiesFile, null);
	}

	public static void propertiesToPojo(Object pojo, File propertiesFile, String prefix) {
		// Parse properties
		try (InputStream is = new FileInputStream(propertiesFile)) {
			final Properties properties = new Properties();
			properties.load(is);
			propertiesToPojo(pojo, properties, prefix);
		} catch (IOException ex) {
			// this should never happen
			throw new RuntimeException(ex);
		}
	}

	public static void propertiesToPojo(Object pojo, Properties properties) {
		propertiesToPojo(pojo, properties, null);
	}

	public static void propertiesToPojo(Object pojo, Properties properties, String prefix) {
		try {
			Class<?> clazz = pojo.getClass();
			for (Field field : clazz.getDeclaredFields()) {
				String fieldName = field.getName();
				String propertyValue = prefix == null ? properties.getProperty(fieldName) : properties.getProperty(prefix + fieldName);
				if (StringUtils.isBlank(propertyValue)) {
					continue;
				}
				Class<?> type = field.getType();
				field.setAccessible(true);
				Object value = null;
				if (type == String.class) {
					value = propertyValue;
				} else if (type == int.class) {
					value = Integer.parseInt(propertyValue);
				} else if (type == boolean.class) {
					value = Boolean.valueOf(propertyValue);
				} else if (type == double.class) {
					value = Double.parseDouble(propertyValue);
				} else if (type == File.class) {
					value = new File(propertyValue);
				}
				if (value != null) {
					field.set(pojo, value);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
