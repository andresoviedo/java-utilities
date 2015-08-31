package org.andresoviedo.util.swing.jnotepad.resources;

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

	public static final String ACTION_COPY = "ACTION_COPY";
	public static final String ACTION_CUT = "ACTION_CUT";
	public static final String ACTION_FIND = "ACTION_FIND";
	public static final String ACTION_FIND_REPLACE = "ACTION_FIND_REPLACE";
	public static final String ACTION_GO_TO_LINE = "ACTION_GO_TO_LINE";
	public static final String ACTION_NEW_FILE = "ACTION_NEW_FILE";
	public static final String ACTION_OPEN_FILE = "ACTION_OPEN_FILE";
	public static final String ACTION_PASTE = "ACTION_PASTE";
	public static final String ACTION_REDO = "ACTION_REDO";
	public static final String ACTION_REPLACE = "ACTION_REPLACE";
	public static final String ACTION_REPLACE_ALL = "ACTION_REPLACE_ALL";
	public static final String ACTION_REPLACE_FIND = "ACTION_REPLACE_FIND";
	public static final String ACTION_SAVE = "ACTION_SAVE";
	public static final String ACTION_SAVE_AS = "ACTION_SAVE_AS";
	public static final String ACTION_SHOW_LINE_NUMBERS = "ACTION_SHOW_LINE_NUMBERS";
	public static final String ACTION_UNDO = "ACTION_UNDO";

	public static final String ACTION_COPY_DESCRIPTION = "ACTION_COPY_DESCRIPTION";
	public static final String ACTION_CUT_DESCRIPTION = "ACTION_CUT_DESCRIPTION";
	public static final String ACTION_FIND_REPLACE_DESCRIPTION = "ACTION_FIND_REPLACE_DESCRIPTION";
	public static final String ACTION_GO_TO_LINE_DESCRIPTION = "ACTION_GO_TO_LINE_DESCRIPTION";
	public static final String ACTION_NEW_FILE_DESCRIPTION = "ACTION_NEW_FILE_DESCRIPTION";
	public static final String ACTION_OPEN_FILE_DESCRIPTION = "ACTION_OPEN_FILE_DESCRIPTION";
	public static final String ACTION_PASTE_DESCRIPTION = "ACTION_PASTE_DESCRIPTION";
	public static final String ACTION_REDO_DESCRIPTION = "ACTION_REDO_DESCRIPTION";
	public static final String ACTION_SAVE_DESCRIPTION = "ACTION_SAVE_DESCRIPTION";
	public static final String ACTION_SAVE_AS_DESCRIPTION = "ACTION_SAVE_AS_DESCRIPTION";
	public static final String ACTION_UNDO_DESCRIPTION = "ACTION_UNDO_DESCRIPTION";

	public static final String LABEL_FIND = "LABEL_FIND";
	public static final String LABEL_REPLACE_WITH = "LABEL_REPLACE_WITH";

	public static final String M_FILE = "M_FILE";
	public static final String M_FILE_MNE = "M_FILE_MNE";

	public static final String M_EDIT = "M_EDIT";
	public static final String M_EDIT_MNE = "M_EDIT_MNE";

	public static final String MESSAGE_INVALID_LINE_NUMBER = "MESSAGE_INVALID_LINE_NUMBER";
	public static final String MESSAGE_NO_MORE_STRINGS_FOUND = "MESSAGE_NO_MORE_STRINGS_FOUND";
	public static final String MESSAGE_STRING_NOT_FOUND = "MESSAGE_STRING_NOT_FOUND";

	public static final String OPTION_CASE_SENSITIVE = "OPTION_CASE_SENSITIVE";
	public static final String OPTION_DIRECTION_BACKWARD = "OPTION_DIRECTION_BACKWARD";
	public static final String OPTION_DIRECTION_FORWARD = "OPTION_DIRECTION_FORWARD";
	public static final String OPTION_MATCH_WHOLE_WORD = "OPTION_MATCH_WHOLE_WORD";
	public static final String OPTION_SCOPE_ALL = "OPTION_SCOPE_ALL";
	public static final String OPTION_SCOPE_SELECTED_LINES = "OPTION_SCOPE_SELECTED_LINES";
	public static final String OPTION_USE_REGEX = "OPTION_USE_REGEX";

	public static final String PATTERN_ERROR_COULDNT_OPEN_FILE = "PATTERN_ERROR_COULDNT_OPEN_FILE";
	public static final String PATTERN_ERROR_COULDNT_SAVE_FILE = "PATTERN_ERROR_COULDNT_SAVE_FILE";
	public static final String PATTERN_TYPE_LINE_NUMBER = "PATTERN_TYPE_LINE_NUMBER";
	public static final String PATTERN_X_OCURRENCES_REPLACED = "PATTERN_X_OCURRENCES_REPLACED";

	public static final String QUESTION_FILE_EXISTS_OVERWRITE = "QUESTION_FILE_EXISTS_OVERWRITE";

	public static final String TITLE_DIRECTION = "TITLE_DIRECTION";
	public static final String TITLE_FIND_REPLACE = "TITLE_FIND_REPLACE";
	public static final String TITLE_OPTIONS = "TITLE_OPTIONS";
	public static final String TITLE_SCOPE = "TITLE_SCOPE";

	/**
	 * The resource bundle used to retrieve the strings.
	 */
	private static ResourceBundle messages;

	/**
	 * Returns the image icon which image is loaded from the specified file (relative to the icons/ directory). May return <code>null</code>
	 * if the image is not found.
	 * 
	 * @param filename
	 *            the filename.
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
	 *            the key for the desired char.
	 * @return the char for the given key.
	 */
	public static char getChar(String key) {
		return messages.getString(key).charAt(0);
	}

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

	/**
	 * Gets a message for the given key, using a MessageFormat with the specified argument.
	 * 
	 * @param key
	 *            the key for the desired string.
	 * @param arg
	 *            the argument to be passed to MessageFormat.
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
	 *            the key for the desired string.
	 * @param args
	 *            the arguments to be passed to MessageFormat.
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