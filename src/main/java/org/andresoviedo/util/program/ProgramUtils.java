package org.andresoviedo.util.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public final class ProgramUtils {

	private final File file;

	public ProgramUtils(String executionStatusFilename) {
		super();
		file = new File(executionStatusFilename);
	}

	public String lastStatus() {
		if (file.exists()) {
			String ret = getLastStatus(file);
			return ret;
		}
		return null;
	}

	public void deleteStatus() {
		if (file.exists()) {
			file.delete();
		}
	}

	private static String getLastStatus(File file) {
		try {
			Scanner scanner = new Scanner(file);
			String time = scanner.next();
			scanner.close();
			return time;
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public void setStatus(String text) {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(text.getBytes());

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}
}
