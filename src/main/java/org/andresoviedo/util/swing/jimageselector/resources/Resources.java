package org.andresoviedo.util.swing.jimageselector.resources;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * The resource class for <code>JNotepad</code>.
 * 

 */
public class Resources {

	public static final String BUTTON_CANCEL = "BUTTON_CANCEL";
	public static final String BUTTON_CAPTURE = "BUTTON_CAPTURE";

	public static final String PATTERN_INVALID_OPACITY = "PATTERN_INVALID_OPACITY";
	public static final String PATTERN_INVALID_SCALE_FACTOR = "PATTERN_INVALID_SCALE_FACTOR";

	public static final String TITLE_CHOOSE_OPACITY_COLOR = "TITLE_CHOOSE_OPACITY_COLOR";
	public static final String TITLE_OPACITY = "TITLE_OPACITY";
	public static final String TITLE_OPERATIONS = "TITLE_OPERATIONS";
	public static final String TITLE_SCALING = "TITLE_SCALING";
	public static final String TITLE_ZOOM = "TITLE_ZOOM";

	/**
	 * The resource bundle used to retrieve the strings.
	 */
	private static ResourceBundle messages;

	/**
	 * Returns the image icon which image is loaded from the specified file (relative to the icons/ directory). May return <code>null</code>
	 * if the image is not found.
	 * 
	 * @param filename
	 *          the filename.
	 * @return the image icon.
	 */
	public static ImageIcon getIcon(String filename) {
		try {
			return new ImageIcon(Resources.class.getResource("icons/" + filename));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * Gets a char for the given key.
	 * 
	 * @param key
	 *          the key for the desired char.
	 * @return the char for the given key.
	 */
	public static char getChar(String key) {
		return messages.getString(key).charAt(0);
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

	/**
	 * Gets a message for the given key, using a MessageFormat with the specified argument.
	 * 
	 * @param key
	 *          the key for the desired string.
	 * @param arg
	 *          the argument to be passed to MessageFormat.
	 * 
	 * @return the message for the given key.
	 */
	public static String getMessage(String key, Object arg) {
		return getMessage(key, new Object[] { arg });
	}

	/**
	 * Gets a message for the given key, using a MessageFormat with the specified arguments.
	 * 
	 * @param key
	 *          the key for the desired string.
	 * @param args
	 *          the arguments to be passed to MessageFormat.
	 * 
	 * @return the message for the given key.
	 */
	public static String getMessage(String key, Object[] args) {
		return new MessageFormat(getString(key)).format(args);
	}

	static {
		try {
			messages = ResourceBundle.getBundle(Resources.class.getPackage().getName() + ".messages", Locale.getDefault());
		} catch (MissingResourceException e) {
		}
	}

}