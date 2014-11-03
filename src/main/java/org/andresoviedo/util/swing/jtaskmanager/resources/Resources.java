package org.andresoviedo.util.swing.jtaskmanager.resources;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public class Resources {

	public static final String TASK_MANAGER_BUTTON_HIDE = "TASK_MANAGER_BUTTON_HIDE";
	public static final String TASK_MANAGER_BUTTON_HIDE_WHEN_POSSIBLE = "TASK_MANAGER_BUTTON_HIDE_WHEN_POSSIBLE";

	public static final String TASK_MANAGER_LABEL_TASKS = "TASK_MANAGER_LABEL_TASKS";

	public static final String TASK_MANAGER_TOOLTIP_CANCEL = "TASK_MANAGER_TOOLTIP_CANCEL";
	public static final String TASK_MANAGER_TOOLTIP_DETAILS = "TASK_MANAGER_TOOLTIP_DETAILS";

	public static final String TASK_MANAGER_TITLE = "TASK_MANAGER_TITLE";

	/**
	 * The resource bundle used to retrieve the strings.
	 */
	private static ResourceBundle messages;

	/**
	 * Returns the image icon loaded from the specified file.
	 * 
	 * @param filename
	 *          the image filename.
	 * @return the image icon loaded from the specified file.
	 */
	public static ImageIcon getIcon(String filename) {
		try {
			return new ImageIcon(Resources.class.getResource("icons/" + filename));
		} catch (Exception e) {
		}
		return null;
	}

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