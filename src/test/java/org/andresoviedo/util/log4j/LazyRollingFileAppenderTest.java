package org.andresoviedo.util.log4j;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LazyRollingFileAppenderTest {

	private final File fileThatShouldntExist = new File(System.getProperty("user.home"), "lazyfileappender_not_created.log");
	private final File fileThatShouldExist = new File(System.getProperty("user.home"), "lazyfileappender_lazy_created.log");

	private final Logger loggerNotUsed = Logger.getLogger(this.getClass().getName() + ".notused");
	private final Logger loggerUsed = Logger.getLogger(this.getClass().getName() + ".used");

	@Before
	public void setUp() {
		// Clean possible previous tests resulted in error
		fileThatShouldExist.delete();

		loggerNotUsed.removeAllAppenders();
		loggerUsed.removeAllAppenders();

		LazyRollingFileAppender notUsedAppender1 = new LazyRollingFileAppender();
		notUsedAppender1.setFile(fileThatShouldntExist.getAbsolutePath());
		notUsedAppender1.activateOptions();
		loggerNotUsed.addAppender(notUsedAppender1);
		loggerNotUsed.setLevel(Level.INFO);

		LazyRollingFileAppender usedAppender2 = new LazyRollingFileAppender();
		usedAppender2.setFile(fileThatShouldExist.getAbsolutePath());
		usedAppender2.setLayout(new SimpleLayout());
		usedAppender2.activateOptions();
		loggerUsed.addAppender(usedAppender2);
		loggerUsed.setLevel(Level.DEBUG);

		Assert.assertFalse(fileThatShouldntExist.exists());
		Assert.assertFalse(fileThatShouldExist.exists());
	}

	@After
	public void after() {
		loggerUsed.removeAllAppenders();
		loggerNotUsed.removeAllAppenders();

		fileThatShouldExist.delete();
	}

	@Test
	public void testFileNotInitialized() {
		loggerUsed.debug("This trace should initialize the lazyfileappender1.log file");
		loggerNotUsed.debug("This trace should not initialize the lazyfileappender2.log file because it is under required level");

		Assert.assertFalse(fileThatShouldntExist.exists());
		Assert.assertTrue(fileThatShouldExist.exists());
	}
}
