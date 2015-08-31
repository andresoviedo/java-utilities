package org.andresoviedo.util.windows;

import org.andresoviedo.util.os.OSUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

public class WindowsUtilsTest {

	private Logger LOG = Logger.getLogger(WindowsUtilsTest.class);

	@Test
	public void testInstallStartMenuItem() throws Exception {
		if (!OSUtils.isWindows()) {
			LOG.warn("Not in Widnows. Ignoring test...");
			return;
		}
		WindowsUtils.installStartMenuItem(WindowsUtils.SPECIALFOLDER_Programs, "my_start_menu", "explorer.exe", "http://www.google.es",
				"Acceso director a google");

		WindowsUtils.uninstallStartMenuItem(WindowsUtils.SPECIALFOLDER_Programs, "my_start_menu");
	}

	@Test
	public void testInstallStartMenuItemForAllUsers() throws Exception {
		if (!OSUtils.isWindows()) {
			LOG.warn("Not in Widnows. Ignoring test...");
			return;
		}
		WindowsUtils.installStartMenuItem(WindowsUtils.SPECIALFOLDER_AllUsersPrograms, "my_start_menu", "explorer.exe",
				"http://www.google.es", "Acceso director a google");

		WindowsUtils.uninstallStartMenuItem(WindowsUtils.SPECIALFOLDER_AllUsersPrograms, "my_start_menu");
	}

}
