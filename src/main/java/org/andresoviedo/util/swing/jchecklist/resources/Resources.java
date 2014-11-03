package org.andresoviedo.util.swing.jchecklist.resources;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Resources class for <code>JCalendar</code>.
 * 

 */
public class Resources {

	public static final String ACTION_CHECK_ALL = "ACTION_CHECK_ALL";
	public static final String ACTION_MOVE_DOWN = "ACTION_MOVE_DOWN";
	public static final String ACTION_MOVE_UP = "ACTION_MOVE_UP";
	public static final String ACTION_TOGGLE = "ACTION_TOGGLE";
	public static final String ACTION_UNCHECK_ALL = "ACTION_UNCHECK_ALL";

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