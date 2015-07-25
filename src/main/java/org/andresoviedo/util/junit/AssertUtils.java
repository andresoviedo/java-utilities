package org.andresoviedo.util.junit;

import org.andresoviedo.util.bean.BeanUtils;
import org.andresoviedo.util.log4j.MemoryAppender;
import org.apache.log4j.Appender;

public final class AssertUtils {

	/**
	 * Fails a test with the given message.
	 * 
	 * @param message
	 *            the identifying message for the {@link AssertionError} (<code>null</code> okay)
	 * @see AssertionError
	 */
	static public void fail(String message) {
		if (message == null) {
			throw new AssertionError();
		}
		throw new AssertionError(message);
	}

	/**
	 * Fails a test with no message.
	 */
	static public void fail() {
		fail(null);
	}

	private static void assertFalse(String message, boolean someMessageExists) {
		if (someMessageExists) {
			fail(message);
		}
	}

	private static void assertTrue(String message, boolean someMessageExists) {
		if (!someMessageExists) {
			fail(message);
		}

	}

	public static void assertLog(Appender mockAppender, String expectedMessage) {
		LogUtils.assertLogs(mockAppender, null, true, expectedMessage);
	}

	public static void assertLog(Appender mockAppender, String message, String expectedMessage) {
		LogUtils.assertLogs(mockAppender, message, true, expectedMessage);
	}

	public static void assertNotLog(Appender mockAppender, String expectedMessage) {
		LogUtils.assertLogs(mockAppender, null, false, expectedMessage);
	}

	public static void assertNotLog(Appender mockAppender, String message, String expectedMessage) {
		LogUtils.assertLogs(mockAppender, message, false, expectedMessage);
	}

	public static void assertLogRegExp(Appender mockAppender, String expectedMessageRegExp) {
		LogUtils.assertLogsRegExp(mockAppender, null, true, expectedMessageRegExp);
	}

	public static void assertLogRegExp(Appender mockAppender, String message, String expectedMessageRegExp) {
		LogUtils.assertLogsRegExp(mockAppender, message, true, expectedMessageRegExp);
	}

	public static void assertNotLogRegExp(Appender mockAppender, String expectedMessageRegExp) {
		LogUtils.assertLogsRegExp(mockAppender, null, false, expectedMessageRegExp);
	}

	public static void assertNotLogRegExp(Appender mockAppender, String message, String expectedMessageRegExp) {
		LogUtils.assertLogsRegExp(mockAppender, message, false, expectedMessageRegExp);
	}

	public static void assertLogs(Appender mockAppender, String... expectedMessages) {
		LogUtils.assertLogs(mockAppender, null, true, expectedMessages);
	}

	public static void assertLogs(Appender mockAppender, String message, String... expectedMessages) {
		LogUtils.assertLogs(mockAppender, message, true, expectedMessages);
	}

	public static void assertNotLogs(Appender mockAppender, String... expectedMessages) {
		LogUtils.assertLogs(mockAppender, null, false, expectedMessages);
	}

	public static void assertNotLogs(Appender mockAppender, String message, String... expectedMessages) {
		LogUtils.assertLogs(mockAppender, message, false, expectedMessages);
	}

	public static void assertLogsRegExp(Appender mockAppender, String... expectedMessagesRegExp) {
		LogUtils.assertLogsRegExp(mockAppender, null, true, expectedMessagesRegExp);
	}

	public static void assertLogsRegExp(Appender mockAppender, String message, String expectedMessagesRegExp[]) {
		LogUtils.assertLogsRegExp(mockAppender, message, true, expectedMessagesRegExp);
	}

	public static void assertNotLogsRegExp(Appender mockAppender, String... expectedMessagesRegExp) {
		LogUtils.assertLogsRegExp(mockAppender, null, false, expectedMessagesRegExp);
	}

	public static void assertNotLogsRegExp(Appender mockAppender, String message, String expectedMessagesRegExp[]) {
		LogUtils.assertLogsRegExp(mockAppender, message, false, expectedMessagesRegExp);
	}

	public static void assertBeanEquals(Object expected, Object actual) {
		assertEquals(null, BeanUtils.reflectionToString(expected), BeanUtils.reflectionToString(actual));
	}

	private static void assertEquals(String mesage, String reflectionToString, String reflectionToString2) {
		if (reflectionToString == null || reflectionToString2 == null) {
			if (reflectionToString != reflectionToString2) {
				fail(mesage);
			}
			return;
		}
		if (!reflectionToString.equals(reflectionToString2)) {
			fail(mesage);
		}
	}

	public static void assertBeanEquals(String message, Object expected, Object actual) {
		assertEquals(message, BeanUtils.reflectionToString(expected), BeanUtils.reflectionToString(actual));
	}

	/**
	 * @see #assertLog(String)
	 * @see #assertNotLog(String)
	 */
	@Deprecated
	public static void assertLog(String expectedMessage, boolean exists) {
		if (exists) {
			LogUtils.assertLogs(null, null, true, expectedMessage);
		} else {
			LogUtils.assertLogs(null, null, false, expectedMessage);
		}
	}

	/**
	 * @see #assertLogs(String)
	 * @see #assertNotLogs(String)
	 */
	@Deprecated
	public static void assertLog(String[] expectedMessage, boolean exists) {
		if (exists) {
			LogUtils.assertLogs(null, null, true, expectedMessage);
		} else {
			LogUtils.assertLogs(null, null, false, expectedMessage);
		}
	}

	static class LogUtils {

		public static void assertLogsRegExp(Appender mockAppender, String message, boolean exists,
				String... expectedMessagesRegExp) {
			// TODO: Implementar la activación dinámica de logs (consola y
			// memoria)
			// if (!this.enabledLogs) {
			// throw new
			// Exception("Do use setUpLogs before using the assertLogs method");
			// }

			if (expectedMessagesRegExp == null || expectedMessagesRegExp.length == 0) {
				throw new IllegalArgumentException("No messages to expect");
			}

			boolean someMessageExists = false;
			boolean[] messagesExist = new boolean[expectedMessagesRegExp.length];

			// Se evalua busca el mensaje esperado en el array de Logs
			// recuperados
			MemoryAppender appender = (MemoryAppender) mockAppender;

			String actual[] = appender.getMessages();

			for (int j = 0; j < expectedMessagesRegExp.length; j++) {
				for (String element : actual) {
					if (element == null) {
						continue;
					}
					if (element.matches(expectedMessagesRegExp[j])) {
						someMessageExists = true;
						messagesExist[j] = true;
						break;
					}
				}
			}

			// con exists == true el usuario pide que existan todos los
			// mensajes
			// con exists == false ningún mensaje debe existir
			if (exists == true) {
				for (int i = 0; i < messagesExist.length; i++) {
					if (!messagesExist[i]) {
						fail("No se ha podido encontrar el log con la regular expressión '" + expectedMessagesRegExp[i]
								+ "'");
					}
				}
			} else {
				assertFalse(message, someMessageExists);
			}

		}

		public static void assertLogs(boolean exists, String... expectedMessages) {
			assertLogs(null, null, exists, expectedMessages);
		}

		public static void assertLogs(Appender mockAppender, String message, boolean exists, String... expectedMessages) {

			// TODO: Implementar la activación dinámica de logs (consola y
			// memoria)
			// if (!this.enabledLogs) {
			// throw new
			// Exception("Do use setUpLogs before using the assertLogs method");
			// }

			if (expectedMessages == null || expectedMessages.length == 0) {
				throw new IllegalArgumentException("No messages to expect");
			}
			boolean[] messagesExist = new boolean[expectedMessages.length];

			// Se evalua busca el mensaje esperado en el array de Logs
			// recuperados
			MemoryAppender appender = (MemoryAppender) mockAppender;

			for (int j = 0; j < expectedMessages.length; j++) {
				for (String element : appender.getMessages()) {
					if (element != null && element.contains(expectedMessages[j])) {
						messagesExist[j] = true;
						break;
					}
				}
			}

			// con exists == true el usuario pide que existan todos los
			// mensajes
			// con exists == false ningún mensaje debe existir
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < messagesExist.length; i++) {
				if (exists ^ messagesExist[i]) {
					sb.append(" '").append(expectedMessages[i]).append("'");
				}
			}
			assertTrue((message != null ? message : "") + (exists ? "No se" : "Se")
					+ " han encontrado los siguientes logs:" + sb.toString(), sb.length() == 0);

		}

	}
}
