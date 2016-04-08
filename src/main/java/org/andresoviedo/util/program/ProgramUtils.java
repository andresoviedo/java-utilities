package org.andresoviedo.util.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Program utilities
 * 
 * @author andresoviedo
 *
 */
public final class ProgramUtils {

	private static final Log LOG = LogFactory.getLog(ProgramUtils.class);

	public static Map<String, Object> getSystemAndEnvPropertiesMap() {
		Map<String, Object> systemProps = new HashMap<String, Object>();
		systemProps.putAll(System.getenv());
		for (Entry<Object, Object> x : System.getProperties().entrySet()) {
			systemProps.put(x.getKey().toString(), x.getValue());
		}
		return systemProps;
	}

	public static class RequestsPerSecondController {

		private final int maxRequestPerSecond;
		private int requestTimes = 0;
		private long time = 0;

		public RequestsPerSecondController(int maxRequestPerSecond) {
			this.maxRequestPerSecond = maxRequestPerSecond;
		}

		public void start() {
			reset();
			LOG.info("Started at " + time);
		}

		public void reset() {
			time = System.currentTimeMillis();
			requestTimes = 0;
		}

		public final synchronized void newRequest() {
			final long newTime = System.currentTimeMillis();
			final long expiratonTime = time + 1000;
			requestTimes++;
			if (newTime < expiratonTime) {
				if (requestTimes >= maxRequestPerSecond) {
					LOG.info("Reached limit! sleeping " + (expiratonTime - newTime) + " millis,,,");
					try {
						Thread.sleep(expiratonTime - newTime);
					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					}
					reset();
				}
			} else { /* if (newTime >= expiratonTime) { */
				requestTimes = 0;
				time = newTime;
			}
		}
	}

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
