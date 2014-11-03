package org.andresoviedo.util.swing.jcalendar;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import org.andresoviedo.util.date.DateUtils;

/**
 * Calendar date label decorator.
 * 

 */
public class CalendarDateLabelDecorator {

	public static final int REPEAT_NONE = 0;
	public static final int REPEAT_DAILY = 1;
	public static final int REPEAT_WEEKLY = 2;
	public static final int REPEAT_MONTHLY = 3;

	/**
	 * Indicates whether this decorator is enabled or not.
	 */
	private boolean enabled = true;

	/**
	 * The background used by the cell renderer when painting the cell.
	 */
	private Color background = Color.red;

	/**
	 * The foreground used by the cell renderer when painting the cell.
	 */
	private Color foreground = Color.white;

	/**
	 * The description used by the cell renderer as the tooltip.
	 */
	private String description;

	/**
	 * The repetition pattern.
	 */
	private int repetitionPattern = REPEAT_NONE;

	/**
	 * The number of days to repeat.
	 */
	private int numberOfDays = 1;

	private int numberOfWeeks = 1;

	private int numberOfMonths = 1;

	/**
	 * The reference date.
	 */
	private Date referenceDate;

	public CalendarDateLabelDecorator(Date referenceDate) {
		if (referenceDate == null) {
			throw new IllegalArgumentException("The reference date is null.");
		}
		this.referenceDate = referenceDate;
	}

	/**
	 * Returns the background used by the cell renderer when painting the cell.
	 * 
	 * @return the background used by the cell renderer when painting the cell.
	 */
	public Color getBackground() {
		return background;
	}

	/**
	 * Sets the background used by the cell renderer when painting the cell.
	 * 
	 * @param background
	 *          an arbitrary color.
	 */
	public void setBackground(Color background) {
		this.background = background;
	}

	/**
	 * Returns the description used by the cell renderer as the tooltip.
	 * 
	 * @return the description used by the cell renderer as the tooltip.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description used by the cell renderer as the tooltip.
	 * 
	 * @param description
	 *          the new description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the foreground used by the cell renderer when painting the cell.
	 * 
	 * @return the foreground used by the cell renderer when painting the cell.
	 */
	public Color getForeground() {
		return foreground;
	}

	/**
	 * Sets the foreground used by the cell renderer when painting the cell.
	 * 
	 * @param foreground
	 *          an arbitrary color.
	 */
	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	/**
	 * Returns whether this decorator is enabled or not.
	 * 
	 * @return <code>true</code>if this decorator is enabled, <code>false</code> otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets whether this decorator is enabled or not.
	 * 
	 * @param enabled
	 *          <code>true</code>if this decorator is enabled, <code>false</code> otherwise.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns the number of days to repeat.
	 * 
	 * @return the number of days to repeat.
	 */
	public int getNumberOfDays() {
		return numberOfDays;
	}

	/**
	 * Sets the number of days to repeat.
	 * 
	 * @param numberOfDays
	 *          the number of days to repeat.
	 * @throws IllegalArgumentException
	 *           if the number of days is less than 1.
	 */
	public void setNumberOfDays(int numberOfDays) {
		if (numberOfDays < 1) {
			throw new IllegalArgumentException("The number of days cannot be less than 1.");
		}
		this.numberOfDays = numberOfDays;
	}

	public int getNumberOfMonths() {
		return numberOfMonths;
	}

	public void setNumberOfMonths(int numberOfMonths) {
		if (numberOfMonths < 1) {
			throw new IllegalArgumentException("The number of months cannot be less than 1.");
		}
		this.numberOfMonths = numberOfMonths;
	}

	public int getNumberOfWeeks() {
		return numberOfWeeks;
	}

	public void setNumberOfWeeks(int numberOfWeeks) {
		if (numberOfWeeks < 1) {
			throw new IllegalArgumentException("The number of weeks cannot be less than 1.");
		}
		this.numberOfWeeks = numberOfWeeks;
	}

	/**
	 * Returns the reference date.
	 * 
	 * @return the reference date.
	 */
	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date date) {
		this.referenceDate = date;
	}

	/**
	 * Returns the repetition pattern.
	 * 
	 * @return the repetition pattern.
	 */
	public int getRepetitionPattern() {
		return repetitionPattern;
	}

	/**
	 * Sets the repetition pattern.
	 * 
	 * @param repetitionPattern
	 *          the repetition pattern.
	 * @throws IllegalArgumentException
	 *           if the repetition pattern is invalid.
	 */
	public void setRepetitionPattern(int repetitionPattern) {
		if ((repetitionPattern != REPEAT_NONE) && (repetitionPattern != REPEAT_DAILY) && (repetitionPattern != REPEAT_WEEKLY)
				&& (repetitionPattern != REPEAT_MONTHLY)) {
			throw new IllegalArgumentException("Invalid repetition pattern: " + repetitionPattern);
		}
		this.repetitionPattern = repetitionPattern;
	}

	public boolean matches(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(DateUtils.resetTimeFields(referenceDate));

		if (date.before(c1.getTime())) {
			return false;
		}

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date);

		do {
			// Match date parts.
			if ((c1.get(Calendar.DATE) == c2.get(Calendar.DATE)) && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
					&& (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))) {
				return true;
			}

			// Increment.
			switch (repetitionPattern) {
			case REPEAT_NONE:
				return false;
			case REPEAT_DAILY:
				c1.add(Calendar.DATE, numberOfDays);
				break;
			case REPEAT_WEEKLY:
				c1.add(Calendar.WEEK_OF_YEAR, numberOfWeeks);
				break;
			case REPEAT_MONTHLY:
				c1.add(Calendar.MONTH, numberOfMonths);
				break;
			}
		} while (!c1.after(c2));

		return false;
	}
}