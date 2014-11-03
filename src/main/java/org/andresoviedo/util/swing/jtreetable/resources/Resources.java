package org.andresoviedo.util.swing.jtreetable.resources;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Resources class for <code>JTreeTable</code>.
 * 

 */
public class Resources {

	public static final String ACTION_DESCRIPTION_COLLAPSE_ALL = "ACTION_DESCRIPTION_COLLAPSE_ALL";
	public static final String ACTION_DESCRIPTION_EXPAND_ALL = "ACTION_DESCRIPTION_EXPAND_ALL";

	public static final String ACTION_NAME_COLLAPSE_ALL = "ACTION_NAME_COLLAPSE_ALL";
	public static final String ACTION_NAME_EXPAND_ALL = "ACTION_NAME_EXPAND_ALL";

	/**
	 * The resource bundle used to retrieve the strings.
	 */
	private static ResourceBundle messages;

	/**
	 * Gets a string for the given key.
	 * 
	 * @param key
	 *          the key for the desired string.
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