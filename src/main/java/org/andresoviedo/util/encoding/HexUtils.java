package org.andresoviedo.util.encoding;

/**
 * A set of general-purpose utilities.
 * 
 */
public class HexUtils {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private HexUtils() {
	}

	/**
	 * Returns the dump representation of a byte buffer in hexadecimal bytes 00 to FF.
	 * 
	 * @param buffer
	 *          the buffer.
	 * @return the dump representation of a byte buffer in hexadecimal bytes 00 to FF.
	 */
	public static String getHexDump(byte[] buffer) {
		String dump = "";
		try {
			int dataLen = buffer.length;
			for (int i = 0; i < dataLen; i++) {
				dump += Character.forDigit((buffer[i] >> 4) & 0x0F, 16);
				dump += Character.forDigit(buffer[i] & 0x0F, 16);
			}
		} catch (Exception e) {
		}
		return dump;
	}

	/**
	 * Returns the dump representation of a byte buffer in hexadecimal bytes 00 to FF. The extra chars are appended before each byte string
	 * representation.
	 * 
	 * @param buffer
	 *          the buffer.
	 * @return the dump representation of a byte buffer in hexadecimal bytes 00 to FF.
	 */
	public static String getHexDump(byte[] buffer, String extraChars) {
		String dump = "";
		try {
			int dataLen = buffer.length;
			for (int i = 0; i < dataLen; i++) {
				dump += extraChars;
				dump += Character.forDigit((buffer[i] >> 4) & 0x0f, 16);
				dump += Character.forDigit(buffer[i] & 0x0f, 16);
			}
		} catch (Exception e) {
		}
		return dump;
	}

	/**
	 * Encodes a positive int as an unsigned byte.
	 * 
	 * @param positive
	 *          the positive int.
	 * @return a positive int encoded as an unsigned byte.
	 */
	public static byte encodeUnsigned(int positive) {
		if (positive < 128) {
			return (byte) positive;
		} else {
			return (byte) (-(256 - positive));
		}
	}

	/**
	 * Returns a random int that can lay from <code>begin</code> to <code>end</code>, both included.
	 */
	public static int getRandomNumber(int begin, int end) {
		double random = Math.random();
		return (int) (random * (end - begin + 1)) + begin;
	}

}