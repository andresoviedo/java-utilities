package org.andresoviedo.util.encoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;

/**
 * This input stream will recognize unicode BOM marks and will skip bytes if getEncoding() method is called before any of the read(...)
 * methods. Invoking read methods without first calling getEncoding will not skip BOM mark.
 * 
 * <p>
 * Usage example:
 * </p>
 * <blockquote>
 * 
 * <pre>
 * String enc = &quot;ISO-8859-1&quot;;
 * FileInputStream fis = new FileInputStream(file);
 * UnicodeInputStream uin = new UnicodeInputStream(fis, enc);
 * enc = uin.getEncoding(); // check for BOM mark and skip bytes
 * InputStreamReader in;
 * if (enc == null) {
 * 	in = new InputStreamReader(uin);
 * } else {
 * 	in = new InputStreamReader(uin, enc);
 * }
 * </pre>
 * 
 * </blockquote>
 * <p>
 * For more information on BOMs, take a look at <a
 * href="http://www.unicode.org/unicode/faq/utf_bom.html">http://www.unicode.org/unicode/faq/utf_bom.html</a>
 * <p>
 * <p>
 * BOMs:
 * </p>
 * <ul>
 * <li>00 00 FE FF (UTF-32, big-endian)</li>;
 * <li>FF FE 00 00 (UTF-32, little-endian)</li>
 * <li>FE FF (UTF-16, big-endian)</li>
 * <li>FF FE (UTF-16, little-endian)</li>
 * <li>EF BB BF (UTF-8)</li>
 * </ul>
 * 
 */
public class UnicodeInputStream extends InputStream {

	private static final int BOM_SIZE = 4;

	private PushbackInputStream internalIn;

	private String defaultEncoding;

	private String encoding;

	private boolean skipBOM = true;

	private boolean initialized;

	/**
	 * Creates a new Unicode input stream with the specified input stream as the underlying stream.
	 * 
	 * @param in
	 *            the underlying input stream.
	 */
	public UnicodeInputStream(InputStream in) {
		this(in, null);
	}

	/**
	 * Creates a new Unicode input stream with the specified input stream as the underlying stream.
	 * 
	 * @param in
	 *            the underlying input stream.
	 * @param defaultEncoding
	 *            the default encoding to use just in case the BOM mark cannot be found.
	 */
	public UnicodeInputStream(InputStream in, String defaultEncoding) {
		internalIn = new PushbackInputStream(in, BOM_SIZE);
		if (defaultEncoding != null) {
			this.defaultEncoding = defaultEncoding;
		} else {
			this.defaultEncoding = (new OutputStreamWriter(new ByteArrayOutputStream())).getEncoding();// System.getProperty("file.encoding");
		}
	}

	/**
	 * Returns whether BOM bytes have to be unread after the initialization or not.
	 * 
	 * @return <code>true</code> if BOM bytes have to be unread after the initialization, <code>false</code> otherwise.
	 */
	public boolean isSkipBOM() {
		return skipBOM;
	}

	/**
	 * Sets whether BOM bytes have to be unread after the initialization or not.
	 * 
	 * @param skipBOM
	 *            <code>true</code> if BOM bytes have to be unread after the initialization, <code>false</code> otherwise.
	 */
	public void setSkipBOM(boolean skipBOM) {
		this.skipBOM = skipBOM;
	}

	/**
	 * Returns the default encoding.
	 * 
	 * @return the default encoding.
	 */
	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	/**
	 * Read encoding based on BOM mark and skip bytes. All non-mark bytes are unread back to the stream.
	 * 
	 * @return the encoding read.
	 */
	public String getEncoding() {
		if (!initialized) {
			try {
				init();
			} catch (IOException e) {
			}
		}
		return encoding;
	}

	/**
	 * Read-ahead four bytes and check for BOM marks. Extra bytes are unread back to the stream, only BOM bytes are skipped.
	 */
	private void init() throws IOException {
		byte bom[] = new byte[BOM_SIZE];
		int n, unread;
		n = internalIn.read(bom, 0, bom.length);

		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
			unread = n - 4;
		} else {
			// Unicode BOM mark not found, unread all bytes
			encoding = defaultEncoding;
			unread = n;
		}

		// Always unread all bytes if skipBOM is false.
		if (!skipBOM) {
			unread = n;
		}
		if (unread > 0) {
			internalIn.unread(bom, (n - unread), unread);
		}
		initialized = true;
	}

	/*
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		initialized = true;
		internalIn.close();
	}

	/*
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		if (!initialized) {
			init();
		}
		return internalIn.read();
	}

}