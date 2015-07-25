package org.andresoviedo.util.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

public class Log4jUtils {

	public static Appender addMemoryAppender(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz);
		return addMemoryAppender(logger);
	}

	public static Appender addMemoryAppender(Logger logger) {
		final String appenderName = logger.getName() + "#" + MemoryAppender.class.getSimpleName();
		Appender appender = logger.getAppender(appenderName);
		if (appender != null) {
			throw new IllegalStateException("There is already a memory appender for '" + logger.getName() + "'");
		}
		appender = new MemoryAppender();
		appender.setName(appenderName);
		logger.addAppender(appender);
		return appender;
	}

	public static void removeMemoryAppender(Appender appender) {
		if (!appender.getName().contains("#")) {
			throw new IllegalArgumentException("Unexpected appender name '" + appender + "'");
		}
		Logger logger = Logger.getLogger(appender.getName().substring(0, appender.getName().indexOf("#")));
		if (logger == null) {
			throw new IllegalArgumentException("There is no logger with the appender '" + appender + "'");
		}
		logger.removeAppender(appender);
	}

}