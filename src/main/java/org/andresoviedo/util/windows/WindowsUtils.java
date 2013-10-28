package org.andresoviedo.util.windows;

import java.io.File;

import org.andresoviedo.util.io.IOHelper;
import org.andresoviedo.util.run.RunHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WindowsUtils {

	private static final Log logger = LogFactory.getLog(WindowsUtils.class);

	public static final String SPECIALFOLDER_AllUsersPrograms = "AllUsersPrograms";
	public static final String SPECIALFOLDER_Programs = "Programs";

	public static final String SOURCE_CREATE_START_MENU_FUNCTION = "/org/andresoviedo/util/windows/createStartMenuItem.vbs";
	public static final String SOURCE_REMOVE_START_MENU_FUNCTION = "/org/andresoviedo/util/windows/removeStartMenuItem.vbs";

	public static void uninstallStartMenuItem(String specialFolder,
			String startMenuItem) throws Exception {
		logger.info("Uninstalling StartMenu item '" + startMenuItem + "'...");
		File tempFile = IOHelper
				.copyResourceToTempFile(SOURCE_REMOVE_START_MENU_FUNCTION);
		try {
			if (RunHelper.exec("cscript.exe", null, tempFile.getAbsolutePath(),
					startMenuItem, specialFolder) != 0) {
				throw new RuntimeException("error");
			}

			logger.info("StartMenu item '" + startMenuItem + "' uninstalled");
		} finally {
			FileUtils.forceDelete(tempFile);
		}
	}

	public static void installStartMenuItem(String specialFolder,
			String startMenuItem, String command, String args,
			String description) throws Exception {
		logger.info("Installing StartMenu item '" + description + "'...");
		File tempFile = IOHelper
				.copyResourceToTempFile(SOURCE_CREATE_START_MENU_FUNCTION);
		try {
			if (RunHelper.exec("cscript.exe", null, tempFile.getAbsolutePath(),
					startMenuItem, command, args, description, specialFolder) != 0) {
				throw new RuntimeException("error");
			}

			logger.info("StartMenu item '" + description + "' installed");
		} finally {
			FileUtils.forceDelete(tempFile);
		}
	}
}
