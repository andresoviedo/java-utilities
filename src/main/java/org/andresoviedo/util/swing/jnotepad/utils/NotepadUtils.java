package org.andresoviedo.util.swing.jnotepad.utils;

import javax.swing.text.Document;

/**
 * Useful methods for <code>JNotepad</code>.
 * 

 */
public class NotepadUtils {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private NotepadUtils() {
	}

	/**
	 * Returns the number of digits of the specified number.
	 * 
	 * @param number
	 *          the number which digits have to be counted.
	 * @return the number of digits of the specified number.
	 */
	public static int getDigitCount(int number) {
		return Integer.toString(number).length();
	}

	/**
	 * Returns the number of lines in a document.
	 * 
	 * @param doc
	 *          the document.
	 * @return the number of lines in a document.
	 */
	public static int getLineCount(Document doc) {
		return doc.getDefaultRootElement().getElementCount();
	}

}