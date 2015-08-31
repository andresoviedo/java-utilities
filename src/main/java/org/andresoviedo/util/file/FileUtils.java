package org.andresoviedo.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * A set of utility methods related to files.
 * 
 */
public class FileUtils {

	/**
	 * Gets the extension of a file, in lowercase. For instance, if file's name is 'test.TXT', the result is 'txt'.
	 * 
	 * @param file
	 *            the file which extension has to be retrieved.
	 * @return the extension of a file, in lowercase.
	 */
	public static String getExtension(File file) {
		String s = file.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			return s.substring(i + 1).toLowerCase();
		}
		return null;
	}

	/**
	 * Copies one file to another. This method uses file channels and has the following issue: if the source file points to the same file as
	 * the destination, the source file will be cleared.
	 * 
	 * @param sourceFile
	 *            the source file.
	 * @param destinationFile
	 *            the destination file.
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	public static void copyFile(File sourceFile, File destinationFile) throws IOException {
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destinationFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				try {
					source.close();
				} catch (IOException e) {
				}
			}
			if (destination != null) {
				try {
					destination.close();
				} catch (IOException e) {
				}
			}
		}
	}

}