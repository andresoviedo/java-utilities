package org.andresoviedo.util.bean;

import java.sql.Time;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The <code>PeriodBean</code> class represents a period of time.
 * 
 * @author aoviedo
 * 
 */
public abstract class PeriodBean {

	/**
	 * Start date of period
	 */
	public Date startDate;
	/**
	 * End date of period
	 */
	public Date endDate;

	/**
	 * The start time in the day
	 */
	public Time startTime;

	/**
	 * Maximum allowed expired time for any execution if called after normal start time.
	 */
	public long allowedExpiredTime = 0;
	/**
	 * The last time of execution
	 */
	public Date lastExecution;
	/**
	 * The calculated time for next execution
	 */
	public Date nextExecution;

	/**
	 * Parses and return a <code>PeriodBean</code> implementation based on the specified definition.
	 * 
	 * @param periodDefinition
	 *          the period definition
	 * @return a <code>PeriodBean</code> implementation
	 */
	public static PeriodBean parsePeriod(String periodDefinition) {
		PeriodBean ret = null;
		if (periodDefinition.matches("(?i)DAILY.*")) {
			ret = DailyPeriod.parse(periodDefinition);
		} else if (periodDefinition.matches("(?i)WEEKLY.*")) {
			ret = WeeklyPeriod.parse(periodDefinition);
		} else {
			throw new IllegalArgumentException("period " + periodDefinition + " undefined. Valid periods are DAILY or WEEKLY");
		}
		return ret;
	}

	/**
	 * The <code>DailyPeriod</code> class represents a time that is repeated during all days.
	 * 
	 * @author aoviedo
	 * 
	 */
	public static class DailyPeriod extends PeriodBean {

		/**
		 * The pattern for parsing daily period definitions
		 */
		static final Pattern pattern = Pattern.compile("(?i)DAILY AT (.+)");

		/**
		 * Constructs a daily period at the specified time.
		 * 
		 * @param time
		 *          the time in the day
		 */
		public DailyPeriod(Time time) {
			this.startTime = time;
		}

		/**
		 * Parses and return a <code>DailyPeriod</code> based on the period definition.<br>
		 * PeriodBean definitions should follow the syntax: DAILY AT &lt;TIME&gt;
		 * 
		 * @param periodDefinition
		 * @return the parsed <code>DailyPeriod</code>
		 */
		public static DailyPeriod parse(String periodDefinition) {
			Matcher m = pattern.matcher(periodDefinition);
			if (m.find()) {
				try {
					return new DailyPeriod(Time.valueOf(m.group(1)));
				} catch (IllegalArgumentException ex) {
					throw new IllegalArgumentException("Daily period definition error. TIME should follow the format HH:mm:ss");
				}
			} else {
				throw new IllegalArgumentException("Daily period definition should follow the next syntax: DAILY AT <TIME>");
			}
		}

		/**
		 * @return the period definition for this DailyPeriod object.
		 */
		public String toString() {
			StringBuffer ret = new StringBuffer("DAILY AT " + startTime.toString());
			return ret.toString();
		}
	}

	/**
	 * The <code>WeeklyPeriod</code> class represents a time or times that are repeated each week.
	 * 
	 * @author aoviedo
	 * 
	 */
	public static class WeeklyPeriod extends PeriodBean {

		/**
		 * The pattern for parsing weekly period definitions
		 */
		static final Pattern pattern = Pattern.compile("(?i)WEEKLY ON (.+) AT (.+)");

		/**
		 * The short week day names to day identifier map
		 */
		static Map<String, Integer> shortWeekDaysToDayIds = new Hashtable<String, Integer>();
		/**
		 * The day identifiers to short week day names map
		 */
		static Map<Integer, String> dayIdsToshortWeekDays = new Hashtable<Integer, String>();
		/**
		 * Short week day names to identifier map initialization
		 */
		static {
			shortWeekDaysToDayIds.put("sun", Calendar.SUNDAY);
			shortWeekDaysToDayIds.put("mon", Calendar.MONDAY);
			shortWeekDaysToDayIds.put("tue", Calendar.TUESDAY);
			shortWeekDaysToDayIds.put("wed", Calendar.WEDNESDAY);
			shortWeekDaysToDayIds.put("thu", Calendar.THURSDAY);
			shortWeekDaysToDayIds.put("fri", Calendar.FRIDAY);
			shortWeekDaysToDayIds.put("sat", Calendar.SATURDAY);
		}
		/**
		 * Day identifiers to short week day names map initialization
		 */
		static {
			dayIdsToshortWeekDays.put(Calendar.SUNDAY, "Sun");
			dayIdsToshortWeekDays.put(Calendar.MONDAY, "Mon");
			dayIdsToshortWeekDays.put(Calendar.TUESDAY, "Tue");
			dayIdsToshortWeekDays.put(Calendar.WEDNESDAY, "Wed");
			dayIdsToshortWeekDays.put(Calendar.THURSDAY, "Thu");
			dayIdsToshortWeekDays.put(Calendar.FRIDAY, "Fri");
			dayIdsToshortWeekDays.put(Calendar.SATURDAY, "Sat");
		}
		/**
		 * The days in the week
		 */
		List<Integer> days;

		/**
		 * Constructs a <code>WeeklyPeriod</code> with the specified days at specified time.
		 * 
		 * @param days
		 *          the days in the week
		 * @param time
		 *          the time in the day
		 */
		public WeeklyPeriod(List<Integer> days, Time time) {
			Collections.sort(days);
			this.days = days;
			this.startTime = time;
		}

		/**
		 * Parses and return a <code>WeeklyPeriod</code> based on the period definition.<br>
		 * PeriodBean definition should follow the syntax: <code>WEEKLY day[|day...] AT &lt;TIME&gt;</code> where <code>day</code> could be one of
		 * <code>Sun</code>,<code>Mon</code>,<code>Tue</code>,<code>Thu</code>,<code>Fri</code>,<code>Sat</code>; and <code>TIME</code> comes in
		 * the format <code>HH:mm:ss</code>.
		 * 
		 * @param periodDefinition
		 *          the period definition
		 * @return the parsed <code>WeeklyPeriod</code>
		 */
		public static WeeklyPeriod parse(String periodDefinition) {
			Matcher m = pattern.matcher(periodDefinition);
			if (m.find()) {
				String[] days = m.group(1).split("\\|");
				List<Integer> daysSet = new Vector<Integer>();
				for (int i = 0; i < days.length; i++) {
					Integer day = shortWeekDaysToDayIds.get(days[i].toLowerCase());
					if (day == null) {
						throw new IllegalArgumentException("day \"" + days[i] + "\" not defined");
					}
					if (daysSet.contains(day)) {
						System.out.println("WARNING: WeeklyPeriod contains duplicated \"" + days[i] + "\" day ");
					} else {
						daysSet.add(day);
					}
				}
				try {
					return new WeeklyPeriod(daysSet, Time.valueOf(m.group(2)));
				} catch (IllegalArgumentException ex) {
					throw new IllegalArgumentException("Weekly period definition error. TIME should follow the format HH:mm:ss");
				}
			} else {
				throw new IllegalArgumentException("Weekly period definition should follow the syntax: WEEKLY day[|day...] AT <TIME>");
			}
		}

		/**
		 * Test whether the specified day it's in the days list.
		 * 
		 * @param day
		 *          the
		 * @return <code>true</true> if the specified day is contained in the days list.
		 */
		public boolean containsDay(int day) {
			return days.contains(day);
		}

		/**
		 * Return the next day in the list starting from the specified reference day.
		 * 
		 * @param referenceDay
		 *          the reference day
		 * @return the next day in the list starting from the specified day
		 */
		public int nextDay(int referenceDay) {
			int i = referenceDay + 1;
			for (int j = referenceDay + 1; j < referenceDay + 7; j++, i++) {
				if (i > 7) {
					i = 1;
				}
				int k = days.indexOf(i);
				if (k >= 0) {
					i = k;
					break;
				}
			}
			return days.get(i);
		}

		/**
		 * Return the number of days remaining until next day in the list starting from the specified reference day.
		 * 
		 * @param referenceDay
		 *          the reference day
		 * @return the next day in the list starting from the specified day
		 */
		public int countToNextDay(int referenceDay) {
			int count = 1;
			int i = referenceDay + 1;
			for (int j = referenceDay + 1; j < referenceDay + 7; j++, i++, count++) {
				if (i > 7) {
					i = 1;
				}
				int k = days.indexOf(i);
				if (k >= 0) {
					i = k;
					break;
				}
			}
			return count;
		}

		/**
		 * @return the period definition for this WeeklyPeriod object.
		 */
		public String toString() {
			StringBuffer ret = new StringBuffer("WEEKLY ON ");
			ret.append(" " + dayIdsToshortWeekDays.get(days.get(0)));
			for (int i = 1; i < days.size(); i++) {
				ret.append("|" + dayIdsToshortWeekDays.get(days.get(i)));
			}
			ret.append(" AT " + startTime.toString());
			return ret.toString();
		}
	}
}
