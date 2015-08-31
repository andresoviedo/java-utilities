package org.andresoviedo.util.swing.jsplitabletabbedpane.resources;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Resources {

	public static final String BUTTON_CLOSE = "BUTTON_CLOSE";
	public static final String BUTTON_CLOSE_ALL = "BUTTON_CLOSE_ALL";
	public static final String BUTTON_CLOSE_OTHERS = "BUTTON_CLOSE_OTHERS";

	/**
	 * The resource bundle used to retrieve the strings.
	 */
	private static ResourceBundle messages;

	/**
	 * Gets a string for the given key.
	 * 
	 * @param key
	 *            the key for the desired string.
	 * @return the string for the given key.
	 */
	public static String getString(String key) {
		try {
			return messages.getString(key);
		} catch (MissingResourceException e) {
			return "--";
		}
	}

	static {
		try {
			messages = ResourceBundle.getBundle(Resources.class.getPackage().getName() + ".messages", Locale.getDefault());
		} catch (MissingResourceException e) {
		}
	}

}