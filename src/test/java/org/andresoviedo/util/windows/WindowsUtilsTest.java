package org.andresoviedo.util.windows;

import org.junit.Test;

public class WindowsUtilsTest {

	@Test
	public void testInstallStartMenuItem() throws Exception {
		WindowsUtils.installStartMenuItem(WindowsUtils.SPECIALFOLDER_Programs,
				"java-utilities-test", "explorer.exe", "http://www.google.es",
				"Acceso director a google");

		WindowsUtils.uninstallStartMenuItem(
				WindowsUtils.SPECIALFOLDER_Programs, "java-utilities-test");
	}

	@Test
	public void testInstallStartMenuItemForAllUsers() throws Exception {
		WindowsUtils.installStartMenuItem(
				WindowsUtils.SPECIALFOLDER_AllUsersPrograms,
				"java-utilities-test", "explorer.exe", "http://www.google.es",
				"Acceso director a google");

		WindowsUtils.uninstallStartMenuItem(
				WindowsUtils.SPECIALFOLDER_AllUsersPrograms,
				"java-utilities-test");
	}

}
