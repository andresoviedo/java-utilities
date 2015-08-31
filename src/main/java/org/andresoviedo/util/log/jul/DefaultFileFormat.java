package org.andresoviedo.util.log.jul;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DefaultFileFormat extends Formatter {

	Date dat = new Date();
	private final static String format = "{0,date} {0,time}";
	private MessageFormat formatter = new MessageFormat(format);

	private Object args[] = new Object[1];

	// Line separator string. This is the value of the line.separator
	// property at the moment that the SimpleFormatter was created.
	private String lineSeparator = System.getProperty("line.separator");

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
		formatter.format(args, text, null);
		sb.append("[");
		sb.append(text);
		sb.append("]");

		// andres
		sb.append(" [Thr-" + format(String.valueOf(record.getThreadID()), 4) + "]");
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
		sb.append(" [" + format(record.getLevel().getLocalizedName(), 7) + "]");
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