package org.andresoviedo.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * A set of useful methods to operate with dates.
 * 
 */
public class DateUtils {

	/**
	 * One second, in milliseconds.
	 */
	public static final int ONE_SECOND = 1000;

	/**
	 * One minute, in milliseconds.
	 */
	public static final int ONE_MINUTE = 60 * ONE_SECOND;

	/**
	 * One hour, in milliseconds.
	 */
	public static final int ONE_HOUR = 60 * ONE_MINUTE;

	/**
	 * One day, in milliseconds.
	 */
	public static final int ONE_DAY = 24 * ONE_HOUR;

	/**
	 * A cache for dateformats.
	 */
	private static Map<String, DateFormat> patterns2DF = new Hashtable<String, DateFormat>();

	/**
	 * The default date format to use.
	 */
	private static final DateFormat DEFAULT_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);

	/**
	 * The default time format to use.
	 */
	private static final DateFormat DEFAULT_TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.MEDIUM);

	/**
	 * The default date-time format to use.
	 */
	private static final DateFormat DEFAULT_DATE_TIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	/**
	 * Parses a date using the specified date pattern.
	 * 
	 * @param date
	 *          the date to parse.
	 * @param pattern
	 *          the date pattern to use.
	 * @return the date object.
	 * @throws ParseException
	 *           if an exception occurs while parsing the date.
	 */
	public static Date parseDate(String date, String pattern) throws ParseException {
		DateFormat df = getDateFormat(pattern);
		synchronized (df) {
			return df.parse(date);
		}
	}

	/**
	 * Parses a date using the default date/time format.
	 * 
	 * @param date
	 *          the date to parse.
	 * @return the date object.
	 * @throws ParseException
	 *           if an exception occurs while parsing the date.
	 */
	public static Date parseDateTime(String date) throws ParseException {
		synchronized (DEFAULT_DATE_TIME_FORMAT) {
			return DEFAULT_DATE_TIME_FORMAT.parse(date);
		}
	}

	/**
	 * Formats a date using the specified pattern.
	 * 
	 * @param date
	 *          the date to format.
	 * @param pattern
	 *          the pattern to use.
	 * @return the formatted date.
	 */
	public static String formatDate(Date date, String pattern) {
		DateFormat df = getDateFormat(pattern);
		synchronized (df) {
			return df.format(date);
		}
	}

	/**
	 * Formats a date using the default date format.
	 * 
	 * @param date
	 *          the date to format.
	 * @return the formatted date.
	 */
	public static String formatDate(Date date) {
		synchronized (DEFAULT_DATE_FORMAT) {
			return DEFAULT_DATE_FORMAT.format(date);
		}
	}

	/**
	 * Formats a date using the default date/time format.
	 * 
	 * @param date
	 *          the date to format.
	 * @return the formatted date.
	 */
	public static String formatDateTime(Date date) {
		synchronized (DEFAULT_DATE_TIME_FORMAT) {
			return DEFAULT_DATE_TIME_FORMAT.format(date);
		}
	}

	/**
	 * Formats a date using the default time format.
	 * 
	 * @param date
	 *          the date to format.
	 * @return the formatted date.
	 */
	public static String formatTime(Date date) {
		synchronized (DEFAULT_TIME_FORMAT) {
			return DEFAULT_TIME_FORMAT.format(date);
		}
	}

	/**
	 * Returns the duration represented by the time part of the specified date, in milliseconds.
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @return the duration represented by the time part of the specified date, in milliseconds.
	 */
	public static long getDuration(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return (c.get(Calendar.HOUR_OF_DAY) * ONE_HOUR) + (c.get(Calendar.MINUTE) * ONE_MINUTE) + (c.get(Calendar.SECOND) * ONE_SECOND);
	}

	/**
	 * Constructs a new date with the date part of a date and the time part of another.
	 * 
	 * @param d1
	 *          the date which date part has to be used.
	 * @param d2
	 *          the date which time part has to be used.
	 * 
	 * @return a new date with the date part of a date and the time part of another.
	 */
	public static Date copyDateFields(Date d1, Date d2) {
		Calendar c = Calendar.getInstance();
		c.setTime(d1);

		int date = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);

		c.setTime(d2);
		c.set(year, month, date);

		return c.getTime();
	}

	/**
	 * Resets the time part of a date.
	 * 
	 * @param date
	 *          the date which part has to be reset.
	 * @return the date with its time portion reset.
	 */
	public static Date resetTimeFields(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		c.set(Calendar.HOUR, c.getMinimum(Calendar.HOUR));
		c.set(Calendar.HOUR_OF_DAY, c.getMinimum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMinimum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMinimum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));

		return c.getTime();
	}

	/**
	 * Returns the specified date with time fields reset. This method is equivalent to <code>resetTimeFields(Date)</code>.
	 * 
	 * @param date
	 *          the date.
	 * @return the same date with time fields reset.
	 */
	public static Date firstSecondOfDay(Date date) {
		return resetTimeFields(date);
	}

	/**
	 * Returns a date representing the first day of the month based on an arbitrary date.
	 * 
	 * @param date
	 *          the date.
	 * @return a date representing the first day of the month based on an arbitrary date.
	 */
	public static Date firstDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, c.getMinimum(Calendar.DATE));

		return c.getTime();
	}

	/**
	 * Returns the specified date with time fields set to their maximum (except <code>Calendar.MILLSECOND</code>, which is set to 0).
	 * 
	 * @param date
	 *          the date.
	 * @return the specified date with time fields set to their maximum.
	 */
	public static Date lastSecondOfDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		c.set(Calendar.HOUR, c.getMaximum(Calendar.HOUR));
		c.set(Calendar.HOUR_OF_DAY, c.getMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.getMaximum(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.getMaximum(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, c.getMinimum(Calendar.MILLISECOND));

		return c.getTime();
	}

	/**
	 * Returns a date representing the last day of the month based on an arbitrary date.
	 * 
	 * @param date
	 *          the date.
	 * @return a date representing the last day of the month based on an arbitrary date.
	 */
	public static Date lastDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));

		return c.getTime();
	}

	/**
	 * Returns the days elapsed between two dates.
	 * 
	 * @param d1
	 *          the first date.
	 * @param d2
	 *          the second date.
	 * @return the days elapsed between two dates.
	 */
	public static int getElapsedDays(Date d1, Date d2) {
		int elapsed = 0;

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(d2.after(d1) ? d1 : d2);
		c2.setTime(d2.after(d1) ? d2 : d1);

		c1.setTime(resetTimeFields(c1.getTime()));
		c2.setTime(resetTimeFields(c2.getTime()));

		while (c1.before(c2)) {
			c1.add(Calendar.DATE, 1);
			elapsed++;
		}
		return elapsed;
	}

	/**
	 * Checks whether the date parts of two dates are equal or not.
	 * 
	 * @param d1
	 *          the first date.
	 * @param d2
	 *          the second date.
	 * @return <code>true</code> if both date parts are equal, <code>false</code> otherwise.
	 */
	public static boolean dateFieldsEqual(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		return (c1.get(Calendar.DATE) == c2.get(Calendar.DATE)) && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
				&& (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
	}

	/**
	 * Adds the specified (signed) amount of time to the given time field.
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @param field
	 *          the name of the field.
	 * @param amount
	 *          the amount of time to add.
	 * @return a new date with the amount of time added.
	 */
	public static Date add(Date date, int field, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(field, amount);

		return c.getTime();
	}

	/**
	 * Returns the appropiate date format for the specified date pattern.
	 * 
	 * @param pattern
	 *          the date pattern.
	 * @return the appropiate date format for the specified pattern.
	 */
	private static DateFormat getDateFormat(String pattern) {
		synchronized (patterns2DF) {
			DateFormat df = patterns2DF.get(pattern);
			if (df == null) {
				df = new SimpleDateFormat(pattern);
				patterns2DF.put(pattern, df);
			}
			return df;
		}
	}

}