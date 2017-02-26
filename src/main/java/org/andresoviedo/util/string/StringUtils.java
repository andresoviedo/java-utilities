package org.andresoviedo.util.string;

import java.text.DecimalFormat;

/**
 * A set of useful methods related to strings.
 * 
 */
public class StringUtils {

	private static final long SECOND = 1000;
	private static final long MINUTE = 60000;
	private static final long HOUR = 3600000;

	private static DecimalFormat DF = new DecimalFormat("00");

	/**
	 * Don't let anyone instantiate this class.
	 */
	private StringUtils() {
	}

	/**
	 * Returns a string representation of the specified amount of time (in milliseconds), using the format 'HH:mm:ss'. For example, if
	 * <code>millis</code> is 3660000, the returned string will be '01:01:00' (one hour and one minute).
	 * 
	 * @param millis
	 *            the amount of time.
	 * @return a string representation of the amount of time.
	 */
	public static String toHHMMSS(long millis) {
		if (millis < 0) {
			millis = 0;
		}
		long hours = millis / HOUR;
		long remaining = millis % HOUR;
		long minutes = remaining / MINUTE;
		remaining = remaining % MINUTE;
		long seconds = remaining / SECOND;
		remaining = remaining % SECOND;

		StringBuffer sb = new StringBuffer();
		synchronized (DF) {
			sb.append(DF.format(hours));
			sb.append(":");
			sb.append(DF.format(minutes));
			sb.append(":");
			sb.append(DF.format(seconds));
		}

		return sb.toString();
	}

	/**
	 * 
	 * This method wraps the text <code>text</code> into lines with a maximum of <code>maxChars</code> characters. This method takes care of
	 * not truncating words.
	 * 
	 * @param text
	 *            the text to split.
	 * @param maxChars
	 *            the maximum number of characters of each line.
	 * @param delim
	 *            <code>null</code> or the text to add to the end of each line (i.e. "&lt;br&gt;").
	 * @return the wrapped text.
	 * @throws IllegalArgumentException
	 *             if <code>maxChars</code> is less than or equal to <code>0</code>.
	 */
	public static String splitText(String text, int maxChars, String delim) {
		if (maxChars <= 0) {
			throw new IllegalArgumentException("maxChars should be greater than 0.");
		}

		String ret = text;
		try {
			if (text.length() > maxChars) {
				ret = "";
				// Cut word when finding an space-char in sentence.
				int tempCharIndex = maxChars - 1;
				while (text.charAt(tempCharIndex) != ' ' && tempCharIndex > 0) {
					tempCharIndex--;
				}

				if (tempCharIndex == 0) {
					tempCharIndex = maxChars;
				}

				ret += text.substring(0, tempCharIndex);
				text = text.substring(tempCharIndex).trim();

				while (text.length() > maxChars) {
					tempCharIndex = maxChars - 1;
					while (text.charAt(tempCharIndex) != ' ' && tempCharIndex > 0) {
						tempCharIndex--;
					}
					if (tempCharIndex == 0) {
						tempCharIndex = maxChars;
					}

					ret += (delim != null ? delim : "") + text.substring(0, tempCharIndex);
					text = text.substring(tempCharIndex).trim();
				}

				if (text.length() > 0) {
					ret += (delim != null ? delim : "") + text;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Returns the specified text with all Unicode control characters removed.
	 * 
	 * @param text
	 *            the text.
	 * @return the text with all Unicode control characters removed.
	 */
	public static String removeUnicodeControlChars(String text) {
		StringBuffer sb = new StringBuffer();

		char c;
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			c = chars[i];
			switch (Character.getType(c)) {
			case Character.CONTROL:
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the next separator index in the specified string, or the length of the string if no separator is found.
	 * 
	 * @param s
	 *            an arbitrary string.
	 * @param from
	 *            the index from which to start looking for separators.
	 * @param separators
	 *            the array of separators.
	 * @return the next separator index in the specified string, or the length of the string if no separator is found.
	 */
	public static int nextSeparator(String s, int from, char[] separators) {
		char c;
		for (final int len = s.length(); from < len; ++from) {
			c = s.charAt(from);
			if (isSeparator(c, separators)) {
				return from;
			}
		}
		return from;
	}

	/**
	 * Returns whether the specified character is among the specified separators.
	 * 
	 * @param c
	 *            an arbitrary character.
	 * @param separators
	 *            the separators array.
	 * @return <code>true</code> if the character is among the separators, <code>false</code> otherwise.
	 */
	private static boolean isSeparator(char c, char[] separators) {
		for (int i = 0; i < separators.length; i++) {
			if (c == separators[i] || (separators[i] == ' ' && Character.isWhitespace(c))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove the lines matching the pattern from the specified text
	 * 
	 * @param text
	 *            input text
	 * @param pattern
	 *            line pattern to remove
	 * @return the string without the lines matching the pattern
	 */
	public static String removeLines(String text, String pattern) {
		StringBuilder ret = new StringBuilder();
		System.out.println("Removing lines from: " + text);
		for (String line : text.split(System.getProperty("line.separator"))) {
			if (line.matches(pattern)) {
				continue;
			}
			System.out.println("no: " + line);
			ret.append(line).append(System.getProperty("line.separator"));
		}
		return ret.toString();
	}

}