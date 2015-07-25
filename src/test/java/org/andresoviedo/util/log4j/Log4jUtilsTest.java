package org.andresoviedo.util.log4j;

import org.andresoviedo.util.junit.AssertUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Log4jUtilsTest {

	Logger sut = Logger.getLogger(Log4jUtilsTest.class);
	Appender appender;

	@Before
	public void setUp() throws Exception {
		appender = Log4jUtils.addMemoryAppender(sut);
	}

	@After
	public void tearDown() throws Exception {
		appender.close();
		Log4jUtils.removeMemoryAppender(appender);
		appender = null;
	}

	@Test
	public void test_memory_appender_and_assert_works() {
		sut.info("hello world!");
		AssertUtils.assertLog(appender, "hello world!");
		System.out.println(appender);
		AssertUtils.assertLogRegExp(appender, "hello .*!");
	}
	
	

}
