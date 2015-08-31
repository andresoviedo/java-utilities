package org.andresoviedo.util.time;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import org.andresoviedo.util.bean.PeriodBean;
import org.andresoviedo.util.bean.PeriodBean.DailyPeriod;
import org.andresoviedo.util.bean.PeriodBean.WeeklyPeriod;

/**
 * This class encapsules util methods to operate with times.
 * 
 * @author andresoviedo
 * 
 */
public class TimeUtils {

	public static void main(String[] args) {
		WeeklyPeriod p = WeeklyPeriod.parse("WEEKLY ON Thu AT 16:53:00");
		p.allowedExpiredTime = 120000;
		System.out.println(calculateNextExecutionDate(p));
	}

	/**
	 * This method calculates, taking a reference time, the next start-end time of a period starting and ending at specified hours.
	 * 
	 * @param timeStart
	 *            the period start hour
	 * @param timeEnd
	 *            the period end hour
	 * @param refTime
	 *            the reference time (current time for example)
	 * @param outStartTime
	 *            the calculated period start time.
	 * @param outEndTime
	 *            the calculated period end time
	 */
	public static void calculateSchedule(Time timeStart, Time timeEnd, Calendar refTime, Calendar outStartTime, Calendar outEndTime) {
		Calendar startTime = Calendar.getInstance();
		{
			// Set start time based on time parameter
			Calendar startTimeHour = Calendar.getInstance();
			startTimeHour.setTime(timeStart);
			startTime.set(Calendar.HOUR_OF_DAY, startTimeHour.get(Calendar.HOUR_OF_DAY));
			startTime.set(Calendar.MINUTE, startTimeHour.get(Calendar.MINUTE));
			startTime.set(Calendar.SECOND, startTimeHour.get(Calendar.SECOND));
		}
		Calendar endTime = Calendar.getInstance();
		{
			// Set end time based on time parameter
			Calendar endTimeParam = Calendar.getInstance();
			endTimeParam.setTime(timeEnd);
			endTime.set(Calendar.HOUR_OF_DAY, endTimeParam.get(Calendar.HOUR_OF_DAY));
			endTime.set(Calendar.MINUTE, endTimeParam.get(Calendar.MINUTE));
			endTime.set(Calendar.SECOND, endTimeParam.get(Calendar.SECOND));
			if (endTime.before(startTime)) {
				// Broadcast end time is at next day!
				if (refTime.after(endTime)) {
					// Broadcast end time is reached now!
					endTime.add(Calendar.DATE, 1);
				} else {
					// Broadcast start time is at previous day!
					startTime.add(Calendar.DATE, -1);
				}
			}
		}
		// If current period is in the past, set period for next day
		if (refTime.after(endTime)) {
			startTime.add(Calendar.DATE, 1);
			endTime.add(Calendar.DATE, 1);
		}

		outStartTime.setTime(startTime.getTime());
		outEndTime.setTime(endTime.getTime());
	}

	/**
	 * Updates the calendar with the time information specified, that is, HOUR_OF_DAY, MINUTE and SECOND.
	 * 
	 * @param calendar
	 *            the calendar to update
	 * @param time
	 *            the time to set to the calendar
	 */
	public static void prepareCalendarForTime(Calendar calendar, Time time) {
		Calendar temp = Calendar.getInstance();
		temp.setTime(time);
		calendar.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, temp.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * Calculate and return the next execution time based on the specified period.
	 * 
	 * @param period
	 *            the period to execute
	 * @return the next execution time based on the specified period
	 */
	public static Date calculateNextExecutionDate(PeriodBean period) {
		// Get current time
		Calendar now = Calendar.getInstance();

		// Initialize next execution time
		Calendar nextExecution = Calendar.getInstance();

		// test type of period object
		if (period instanceof DailyPeriod) {
			// daily execution
			DailyPeriod dailyPeriod = (DailyPeriod) period;

			if (period.startDate != null && period.startDate.after(now.getTime())) {
				// start date range it's in the future
				nextExecution.setTime(period.startDate);
			}
			// prepare next execution time
			TimeUtils.prepareCalendarForTime(nextExecution, dailyPeriod.startTime);

			// test case even when expired
			if (now.getTimeInMillis() > nextExecution.getTimeInMillis() + period.allowedExpiredTime
					|| (nextExecution.getTime().equals(period.lastExecution))) {
				// start time it's in the past
				nextExecution.add(Calendar.DATE, 1);
			}

			if (period.startDate != null && nextExecution.getTime().before(period.startDate)) {
				// start date range is in the future
				nextExecution.add(Calendar.DATE, 1);
			}

		} else if (period instanceof WeeklyPeriod) {
			// weekly execution
			WeeklyPeriod weeklyPeriod = (WeeklyPeriod) period;

			if (period.startDate != null && period.startDate.after(now.getTime())) {
				// start date range it's in the future
				nextExecution.setTime(period.startDate);
			}

			// prepare next execution time
			TimeUtils.prepareCalendarForTime(nextExecution, weeklyPeriod.startTime);

			// get next execution day
			int firstDayExecution = nextExecution.get(Calendar.DAY_OF_WEEK);

			if (!weeklyPeriod.containsDay(firstDayExecution)
					|| (now.getTimeInMillis() > nextExecution.getTimeInMillis() + period.allowedExpiredTime)
					|| (nextExecution.getTime().equals(period.lastExecution))) {
				// start time it's in the past or period does not contains the first day of execution
				nextExecution.add(Calendar.DATE, weeklyPeriod.countToNextDay(firstDayExecution));
			}

			if (period.startDate != null && nextExecution.getTime().before(period.startDate)) {
				// start date range is in the future
				nextExecution.add(Calendar.DATE, weeklyPeriod.countToNextDay(firstDayExecution));
			}
		}
		return nextExecution.getTime();
	}
}
