package org.andresoviedo.util.log.jul;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerFactory {
	public static Logger getLogger(String name) {
		return new ApplicationLogger(name).getLogger();
	}

	public static Logger getLogger(String name, String fileName, Level level,
			int numFiles, int fileSize) {
		return new ApplicationLogger(name, fileName, level, numFiles, fileSize)
				.getLogger();
	}
}

class ApplicationLogger {
	private String _name;
	private String _fileName;
	private Level _level;
	private int _numFiles;
	private int _fileSize;

	private Logger _logger = Logger.getLogger("");

	private static Map<String, Logger> loggers = new Hashtable<String, Logger>();

	ApplicationLogger(String name) {
		this(name, ".\\logs\\" + name + "\\" + name + ".log", Level.INFO, 1,
				1073741824);
	}

	ApplicationLogger(String name, String fileName, Level level, int numFiles,
			int fileSize) {
		_name = name;
		_fileName = fileName;
		_level = level;
		_numFiles = numFiles;
		_fileSize = fileSize;
		init();
	}

	private void init() {
		try {
			_logger.logp(Level.INFO, "AppLogger.class", "init",
					"Looking for logger [" + _name + "]...");
			// _logger = LogManager.getLogManager().getLogger(_name);
			_logger = loggers.get(_name);
			if (_logger == null) {
				// Prepare file
				File temp = new File(_fileName);

				// Prepare dir
				if (temp.getParent() != null) {
					new File(temp.getParent()).mkdirs();
				}

				// Prepare file handler
				FileHandler fh = new FileHandler(temp.getAbsolutePath(),
						_fileSize, _numFiles, true);
				fh.setEncoding("UTF-8");
				MyFormatter mf = new MyFormatter();
				fh.setFormatter(mf);

				// Prepare logger
				_logger = Logger.getLogger(_name);
				_logger.setLevel(_level);
				_logger.addHandler(fh);

				// Add SystemLog handler
				_logger.addHandler(new SystemLogHandler());

				// Register logger
				// java.util.logging.LogManager lm =
				// java.util.logging.LogManager.getLogManager();
				// lm.addLogger(_logger);
				loggers.put(_name, _logger);

				_logger.logp(Level.INFO, "AppLogger", "initializeLogger",
						"Logger initialized..." + " name[" + _name + "]"
								+ " fileName[" + _fileName + "]" + " level["
								+ _level + "]" + " numFiles[" + _numFiles + "]"
								+ " fileSize[" + _fileSize + "]...");
			} else {
				_logger.logp(Level.INFO, "AppLogger.class", "init",
						"Looking for logger [" + _name + "]... Found");
			}
		} catch (Exception ex) {
			_logger.throwing("AppLogger.class", "init", ex);
		}
	}

	Logger getLogger() {
		return _logger;
	}

	class MyFormatter extends Formatter {
		Date dat = new Date();
		private final static String format = "{0,date} {0,time}";
		private MessageFormat formatter;

		private Object args[] = new Object[1];

		// Line separator string. This is the value of the line.separator
		// property at the moment that the SimpleFormatter was created.
		private String lineSeparator = (String) System
				.getProperty("line.separator");

		/**
		 * Format the given LogRecord.
		 * 
		 * @param record
		 *            the log record to be formatted.
		 * @return a formatted log record
		 */
		public synchronized String format(LogRecord record) {
			StringBuffer sb = new StringBuffer();
			// Minimize memory allocations here.
			dat.setTime(record.getMillis());
			args[0] = dat;
			StringBuffer text = new StringBuffer();
			if (formatter == null) {
				formatter = new MessageFormat(format);
			}
			formatter.format(args, text, null);
			sb.append("[");
			sb.append(text);
			sb.append("]");

			// andres
			sb.append(" [Thr-"
					+ format(String.valueOf(record.getThreadID()), 4) + "]");
			// andres

			sb.append(" [");
			if (record.getSourceClassName() != null) {
				sb.append(format(record.getSourceClassName(), 20));
			} else {
				sb.append(format(record.getLoggerName(), 20));
			}
			sb.append("]");
			if (record.getSourceMethodName() != null) {
				sb.append(" [");
				sb.append(format(record.getSourceMethodName(), 20));
				sb.append("]");
			}

			String message = formatMessage(record);
			sb.append(" [" + format(record.getLevel().getLocalizedName(), 7)
					+ "]");
			sb.append(" " + message);
			if (record.getThrown() != null) {
				try {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					record.getThrown().printStackTrace(pw);
					pw.close();
					sb.append(sw.toString());
				} catch (Exception ex) {
				}
			}
			sb.append(lineSeparator);
			return sb.toString();
		}

		private String format(String text, int length) {
			String ret = null;
			if (text.length() > length) {
				ret = text.substring(0, length);
			} else {
				int n = length - text.length();
				ret = text;
				for (int i = 0; i < n; i++) {
					ret += " ";
				}
			}
			return ret;
		}
	}

	class SystemLogHandler extends Handler {
		public void close() {
		}

		public void flush() {
		}

		public void publish(LogRecord record) {
			if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
				String sourceClassName = record.getSourceClassName();
				String methodName = record.getSourceMethodName();
				String message = record.getMessage();
				System.err.println(sourceClassName != null ? sourceClassName
						: "" + methodName != null ? methodName
								: "" + message != null ? message : "");
				Throwable thrown = record.getThrown();
				if (thrown != null) {
					thrown.printStackTrace();
				}
			}
		}
	}
}
