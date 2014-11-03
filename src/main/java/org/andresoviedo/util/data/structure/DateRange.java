package org.andresoviedo.util.data.structure;

import java.util.Calendar;
import java.util.Date;

import org.andresoviedo.util.date.DateUtils;

/**
 * A date range.
 * 

 */
public class DateRange {

	/**
	 * The start date (in milliseconds). This field is immutable.
	 */
	private long startTime;

	/**
	 * The end date (in milliseconds). This field is immutable.
	 */
	private long endTime;

	/**
	 * Constructs a new date range.
	 * 
	 * @param start
	 *          the start date.
	 * @param end
	 *          the end date.
	 * @throws IllegalArgumentException
	 *           if either the start date or the end date are <code>null</code>, or if the start date is after the end date.
	 */
	public DateRange(Date start, Date end) {
		if (start == null) {
			throw new IllegalArgumentException("The start date is null.");
		}
		if (end == null) {
			throw new IllegalArgumentException("The end date is null.");
		}
		if (start.after(end)) {
			throw new IllegalArgumentException("The start date should be before the end date.");
		}
		this.startTime = start.getTime();
		this.endTime = end.getTime();
	}

	/**
	 * Returns the duration of this date range, in milliseconds.
	 * 
	 * @return the duration of this date range, in milliseconds.
	 */
	public long getDuration() {
		return (endTime - startTime);
	}

	/**
	 * Returns the end date. Any modification made to the returned date won't affect the date range, since a new date object is created.
	 * 
	 * @return the end date.
	 */
	public Date getEnd() {
		return new Date(endTime);
	}

	/**
	 * Returns the end date, in milliseconds.
	 * 
	 * @return the end date, in milliseconds.
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * Returns the start date. Any modification made to the returned date won't affect the date range, since a new date object is created.
	 * 
	 * @return the start date.
	 */
	public Date getStart() {
		return new Date(startTime);
	}

	/**
	 * Returns the start date, in milliseconds.
	 * 
	 * @return the start date, in milliseconds.
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Returns whether the specified date is included in this date range. Lower and upper bounds are included.
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @return <code>true</code> if the date is included, <code>false</code> otherwise.
	 * @throws IllegalArgumentException
	 *           if <code>date</code> is <code>null</code>.
	 */
	public boolean includes(Date date) {
		return includes(date, false, false);
	}

	/**
	 * Returns whether the specified date is included in this date range. Lower and upper bounds are included.
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @return <code>true</code> if the date is included, <code>false</code> otherwise.
	 */
	public boolean includes(long date) {
		return includes(date, false, false);
	}

	/**
	 * Returns whether the specified date is included in this date range.
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @param excludeLowerBound
	 *          indicates whether to include or exclude the lower bound. Excluding the lower bound means that if the supplied date is equal to
	 *          the lower bound, it won't be considered to be included in the date range.
	 * @param excludeUpperBound
	 *          indicates whether to include or exclude the upper bound. Excluding the upper bound means that if the supplied date is equal to
	 *          the upper bound, it won't be considered to be included in the date range.
	 * @return <code>true</code> if the date is included, <code>false</code> otherwise.
	 * @throws IllegalArgumentException
	 *           if <code>date</code> is <code>null</code>.
	 */
	public boolean includes(Date date, boolean excludeLowerBound, boolean excludeUpperBound) {
		if (date == null) {
			throw new IllegalArgumentException("The date is null.");
		}
		return includes(date.getTime(), excludeLowerBound, excludeUpperBound);
	}

	/**
	 * Returns whether the specified date is included in this date range.
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @param excludeLowerBound
	 *          indicates whether to include or exclude the lower bound. Excluding the lower bound means that if the supplied date is equal to
	 *          the lower bound, it won't be considered to be included in the date range.
	 * @param excludeUpperBound
	 *          indicates whether to include or exclude the upper bound. Excluding the upper bound means that if the supplied date is equal to
	 *          the upper bound, it won't be considered to be included in the date range.
	 * @return <code>true</code> if the date is included, <code>false</code> otherwise.
	 */
	public boolean includes(long date, boolean excludeLowerBound, boolean excludeUpperBound) {
		boolean b1 = excludeLowerBound ? date > startTime : date >= startTime;
		boolean b2 = excludeUpperBound ? date < endTime : date <= endTime;
		return (b1 && b2);
	}

	/**
	 * Returns whether the time part of the specified date is included in this date range (which can also be seen as a time range).Lower and
	 * upper bounds are included.
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @return <code>true</code> if the time part of the date is included, <code>false</code> otherwise.
	 */
	public boolean includesTimePart(long date) {
		return includesTimePart(date, false, false);
	}

	/**
	 * Returns whether the time part of the specified date is included in this date range (which can also be seen as a time range).Lower and
	 * upper bounds are included.
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @return <code>true</code> if the time part of the date is included, <code>false</code> otherwise.
	 * @throws IllegalArgumentException
	 *           if <code>date</code> is <code>null</code>.
	 */
	public boolean includesTimePart(Date date) {
		return includesTimePart(date, false, false);
	}

	/**
	 * Returns whether the time part of the specified date is included in this date range (which can also be seen as a time range).
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @param excludeLowerBound
	 *          indicates whether to include or exclude the lower bound. Excluding the lower bound means that if the supplied date is equal to
	 *          the lower bound, it won't be considered to be included in the date range.
	 * @param excludeUpperBound
	 *          indicates whether to include or exclude the upper bound. Excluding the upper bound means that if the supplied date is equal to
	 *          the upper bound, it won't be considered to be included in the date range.
	 * @return <code>true</code> if the time part of the date is included, <code>false</code> otherwise.
	 */
	public boolean includesTimePart(long date, boolean excludeLowerBound, boolean excludeUpperBound) {
		return includesTimePart(new Date(date), excludeLowerBound, excludeUpperBound);
	}

	/**
	 * Returns whether the time part of the specified date is included in this date range (which can also be seen as a time range).
	 * 
	 * @param date
	 *          an arbitrary date.
	 * @param excludeLowerBound
	 *          indicates whether to include or exclude the lower bound. Excluding the lower bound means that if the supplied date is equal to
	 *          the lower bound, it won't be considered to be included in the date range.
	 * @param excludeUpperBound
	 *          indicates whether to include or exclude the upper bound. Excluding the upper bound means that if the supplied date is equal to
	 *          the upper bound, it won't be considered to be included in the date range.
	 * @return <code>true</code> if the time part of the date is included, <code>false</code> otherwise.
	 * @throws IllegalArgumentException
	 *           if <code>date</code> is <code>null</code>.
	 */
	public boolean includesTimePart(Date date, boolean excludeLowerBound, boolean excludeUpperBound) {
		if (date == null) {
			throw new IllegalArgumentException("The date is null.");
		}
		Date d1 = DateUtils.copyDateFields(date, getStart());
		Date d2 = DateUtils.copyDateFields(date, getEnd());
		if (d1.after(d2)) {
			d2 = DateUtils.add(d2, Calendar.DATE, 1);
		}

		boolean b1 = excludeLowerBound ? date.after(d1) : !date.before(d1);
		boolean b2 = excludeUpperBound ? date.before(d2) : !date.after(d2);

		return (b1 && b2);
	}

}