package org.andresoviedo.util.swing.jcalendar;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.andresoviedo.util.date.DateUtils;

/**
 * Calendar table model.
 * 
 */
public class CalendarTableModel extends AbstractTableModel {

	public static class DateLocation {

		public int column;

		public long date;
		public int row;

		public DateLocation(long date, int row, int column) {
			this.date = date;
			this.row = row;
			this.column = column;
		}

		public int getColumn() {
			return column;
		}

		public long getDate() {
			return date;
		}

		public int getRow() {
			return row;
		}

	}

	/**
	 * A constant indicating that a custom number of months (set by the user) has to be displayed.
	 */
	public static final int DISPLAY_CUSTOM_MONTHS = 3;

	/**
	 * A constant indicating that a custom number of weeks (set by the user) has to be displayed.
	 */
	public static final int DISPLAY_CUSTOM_WEEKS = 2;

	/**
	 * A constant indicating that the entire or the partial month has to be displayed.
	 */
	public static final int DISPLAY_MONTH = 0;

	/**
	 * A constant indicating that the entire or the partial year has to be displayed.
	 */
	public static final int DISPLAY_YEAR = 1;

	/**
	 * The column headers.
	 */
	private static String[] COLUMN_HEADERS;

	static {
		String[] days = new DateFormatSymbols().getShortWeekdays();

		COLUMN_HEADERS = new String[7];
		COLUMN_HEADERS[0] = days[Calendar.MONDAY].substring(0, 1);
		COLUMN_HEADERS[1] = days[Calendar.TUESDAY].substring(0, 1);
		COLUMN_HEADERS[2] = days[Calendar.WEDNESDAY].substring(0, 1);
		COLUMN_HEADERS[3] = days[Calendar.THURSDAY].substring(0, 1);
		COLUMN_HEADERS[4] = days[Calendar.FRIDAY].substring(0, 1);
		COLUMN_HEADERS[5] = days[Calendar.SATURDAY].substring(0, 1);
		COLUMN_HEADERS[6] = days[Calendar.SUNDAY].substring(0, 1);
	}

	/**
	 * Indicates whether the entire month has to be displayed or not. If <code>false</code>, the first displayed week of the month will be
	 * the one specified by the reference date.
	 */
	private boolean displayEntireMonth = true;

	/**
	 * Indicates whether the entire year has to be displayed or not. This flag applies only when the <code>DISPLAY_YEAR</code> display mode
	 * is set. If <code>false</code>, the first month displayed will be the one specified by the reference date.
	 */
	private boolean displayEntireYear = false;

	/**
	 * The display mode.
	 */
	private int displayMode;

	/**
	 * First day of month locations, useful for painting stuff.
	 */
	private DateLocation[] firstDayOfMonthLocations;

	/**
	 * The number of months to be displayed (used in conjunction with the DISPLAY_CUSTOM_MONTHS display mode).
	 */
	private int numberOfMonths = 1;

	/**
	 * The number of weeks to be displayed (used in conjunction with the DISPLAY_CUSTOM_WEEKS display mode).
	 */
	private int numberOfWeeks = 1;

	/**
	 * The reference date.
	 */
	private Date referenceDate;

	/**
	 * The values. Each value corresponds to a date (in milliseconds) with time fields reset.
	 */
	private long[][] values;

	/**
	 * Constructs a new calendar table model, with the current date as the reference date. The display mode is <code>DISPLAY_YEAR</code>.
	 */
	public CalendarTableModel() {
		this(new Date());
	}

	/**
	 * Constructs a new calendar table model, with the specified date as the reference date. The display mode is <code>DISPLAY_YEAR</code>.
	 * 
	 * @param referenceDate
	 *            the reference date.
	 * @throws IllegalArgumentException
	 *             if the reference date is <code>null</code>.
	 */
	public CalendarTableModel(Date referenceDate) {
		this(referenceDate, DISPLAY_YEAR);
	}

	/**
	 * Constructs a new calendar table model, with the specified date and display mode.
	 * 
	 * @param referenceDate
	 *            the reference date.
	 * @param displayMode
	 *            the display mode.
	 * @throws IllegalArgumentException
	 *             if the reference date is <code>null</code> or an invalid display mode is specified.
	 */
	public CalendarTableModel(Date referenceDate, int displayMode) {
		if (referenceDate == null) {
			throw new IllegalArgumentException("The reference date is null.");
		}
		this.checkDisplayMode(displayMode);
		this.referenceDate = referenceDate;
		this.displayMode = displayMode;
		this.initialize();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_HEADERS.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_HEADERS[column];
	}

	/**
	 * Returns the date at the specified row and column.
	 * 
	 * @param rowIndex
	 *            the row index.
	 * @param columnIndex
	 *            the column index.
	 * @return the date at the specified row and column.
	 */
	public Date getDateValueAt(int rowIndex, int columnIndex) {
		return new Date(values[rowIndex][columnIndex]);
	}

	/**
	 * Returns the display mode.
	 * 
	 * @return the display mode.
	 */
	public int getDisplayMode() {
		return displayMode;
	}

	/**
	 * Returns the first date displayed.
	 * 
	 * @return the first date displayed.
	 */
	public Date getFirstDate() {
		return getDateValueAt(0, 0);
	}

	/**
	 * Returns the last date displayed.
	 * 
	 * @return the last date displayed.
	 */
	public Date getLastDate() {
		return getDateValueAt(getRowCount() - 1, getColumnCount() - 1);
	}

	/**
	 * Returns the number of months that have to be displayed when the display mode is set to <code>DISPLAY_CUSTOM_MONTHS</code>.
	 * 
	 * @return the number of months that have to be displayed when the display mode is set to <code>DISPLAY_CUSTOM_MONTHS</code>.
	 */
	public int getNumberOfMonths() {
		return numberOfMonths;
	}

	/**
	 * Returns the number of weeks that have to be displayed when the display mode is set to <code>DISPLAY_CUSTOM_WEEKS</code>.
	 * 
	 * @return the number of weeks that have to be displayed when the display mode is set to <code>DISPLAY_CUSTOM_WEEKS</code>.
	 */
	public int getNumberOfWeeks() {
		return numberOfWeeks;
	}

	/**
	 * Returns the reference date.
	 * 
	 * @return the reference date.
	 */
	public Date getReferenceDate() {
		return referenceDate;
	}

	@Override
	public int getRowCount() {
		return values.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return new Long(values[rowIndex][columnIndex]);
	}

	/**
	 * Returns whether the entire month has to be displayed or not. If <code>false</code>, the first displayed week of the month will be the
	 * one specified by the reference date.
	 * 
	 * @return <code>true</code> if the entire month is displayed, <code>false</code> otherwise.
	 */
	public boolean isDisplayEntireMonth() {
		return displayEntireMonth;
	}

	/**
	 * Returns whether the entire year has to be displayed or not. This flag applies only when the <code>DISPLAY_YEAR</code> display mode is
	 * set. If <code>false</code>, the first month displayed will be the one specified by the reference date.
	 * 
	 * @return <code>true</code> if the entire year is displayed, <code>false</code> otherwise.
	 */
	public boolean isDisplayEntireYear() {
		return displayEntireYear;
	}

	/**
	 * Sets whether the entire month is displayed or not. This only makes sense when using the <code>DISPLAY_MONTH</code> display mode.
	 * 
	 * @param displayEntireMonth
	 *            <code>true</code> if the entire month has to be displayed, <code>false</code> otherwise.
	 */
	public void setDisplayEntireMonth(boolean displayEntireMonth) {
		if (this.displayEntireMonth != displayEntireMonth) {
			this.displayEntireMonth = displayEntireMonth;
			initialize();
			fireTableDataChanged();
		}
	}

	/**
	 * Sets whether the entire year is displayed or not. This flag applies only when the <code>DISPLAY_YEAR</code> display mode is set.
	 * Notice that this method does not switch to the <code>DISPLAY_YEAR</code> display mode.
	 * 
	 * @param displayEntireYear
	 *            <code>true</code> if the entire year has to be displayed, <code>false</code> otherwise.
	 */
	public void setDisplayEntireYear(boolean displayEntireYear) {
		if (this.displayEntireYear != displayEntireYear) {
			this.displayEntireYear = displayEntireYear;
			if (displayMode == DISPLAY_YEAR) {
				initialize();
				fireTableDataChanged();
			}
		}
	}

	/**
	 * Sets the display mode.
	 * 
	 * @param displayMode
	 *            the new display mode.
	 * @throws IllegalArgumentException
	 *             if the display mode is not valid.
	 */
	public void setDisplayMode(int displayMode) {
		if (this.displayMode == displayMode) {
			return;
		}
		this.checkDisplayMode(displayMode);
		this.displayMode = displayMode;
		this.initialize();
		this.fireTableDataChanged();
	}

	/**
	 * Sets the number of months that have to be displayed when the display mode is set to <code>DISPLAY_CUSTOM_MONTHS</code>. If the
	 * current display mode is <code>DISPLAY_CUSTOM_MONTHS</code>, the data is reloaded.
	 * 
	 * @param numberOfMonths
	 *            the number of months to be displayed.
	 * @throws IllegalArgumentException
	 *             if the number of months is less than 1.
	 */
	public void setNumberOfMonths(int numberOfMonths) {
		if (numberOfMonths < 1) {
			throw new IllegalArgumentException("The number of months cannot be less than 1.");
		}
		if (this.numberOfMonths != numberOfMonths) {
			this.numberOfMonths = numberOfMonths;
			if (displayMode == DISPLAY_CUSTOM_MONTHS) {
				initialize();
				fireTableDataChanged();
			}
		}
	}

	/**
	 * Sets the number of weeks that have to be displayed when the display mode is set to <code>DISPLAY_CUSTOM_WEEKS</code>. If the current
	 * display mode is <code>DISPLAY_CUSTOM_WEEKS</code>, the data is reloaded.
	 * 
	 * @param numberOfWeeks
	 *            the number of weeks to be displayed.
	 * @throws IllegalArgumentException
	 *             if the number of weeks is less than 1.
	 */
	public void setNumberOfWeeks(int numberOfWeeks) {
		if (this.numberOfWeeks != numberOfWeeks) {
			this.numberOfWeeks = numberOfWeeks;
			if (displayMode == DISPLAY_CUSTOM_WEEKS) {
				initialize();
				fireTableDataChanged();
			}
		}
	}

	/**
	 * Sets a new reference date. The data will be reloaded based on current settings.
	 * 
	 * @param date
	 *            a new reference date.
	 * @throws IllegalArgumentException
	 *             if the specified date is <code>null</code>.
	 */
	public void setReferenceDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("The reference date is null.");
		}
		this.referenceDate = date;
		initialize();
		fireTableDataChanged();
	}

	/**
	 * Returns the date location associated to the specified date. May return <code>null</code> if the date is not in the model.
	 * 
	 * @param date
	 *            the date.
	 * @return the date location associated to the specified date.
	 */
	DateLocation getDateLocation(Date date) {
		// Normalize the date.
		long millis = DateUtils.resetTimeFields(date).getTime();

		// Could be better implemented.
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getColumnCount(); j++) {
				if (millis == values[i][j]) {
					return new DateLocation(values[i][j], i, j);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the first date of month location associated to the specified date.
	 * 
	 * @param date
	 *            the date.
	 * @return the first date of month location associated to the specified date.
	 */
	DateLocation getFirstDayOfMonthLocation(Date date) {
		long millis = DateUtils.resetTimeFields(DateUtils.firstDayOfMonth(date)).getTime();
		for (int i = 0; i < firstDayOfMonthLocations.length; i++) {
			if (firstDayOfMonthLocations[i].getDate() == millis) {
				return firstDayOfMonthLocations[i];
			}
		}
		return null;
	}

	/**
	 * Returns the array of the first day of month locations.
	 * 
	 * @return the array of the first day of month locations.
	 */
	DateLocation[] getFirstDayOfMonthLocations() {
		return firstDayOfMonthLocations;
	}

	/**
	 * Checks whether the specified display mode is a valid display mode.
	 * 
	 * @param displayMode
	 *            the display mode to check.
	 * @throws IllegalArgumentException
	 *             if the display mode is not valid.
	 */
	private void checkDisplayMode(int displayMode) {
		if ((displayMode != DISPLAY_MONTH) && (displayMode != DISPLAY_YEAR) && (displayMode != DISPLAY_CUSTOM_WEEKS)
				&& (displayMode != DISPLAY_CUSTOM_MONTHS)) {
			throw new IllegalArgumentException("Invalid display mode: " + displayMode);
		}
	}

	/**
	 * Computes the number of weeks to display to ensure that both dates will be displayed.
	 * 
	 * @param d1
	 *            the first date.
	 * @param d2
	 *            the second date.
	 * @return the number of weeks to display to ensure that both dates will be displayed.
	 */
	private int getNumberOfWeeksToDisplay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		int result = 0;
		while (c1.before(c2)) {
			result++;
			c1.add(Calendar.DATE, 7);
		}
		if (c1.get(Calendar.WEEK_OF_YEAR) == (c2.get(Calendar.WEEK_OF_YEAR))) {
			result++;
		}

		return result;
	}

	/**
	 * Initializes table model's data.
	 */
	private void initialize() {
		switch (displayMode) {
		case DISPLAY_MONTH:
		case DISPLAY_YEAR:
			initialize0();
			break;
		case DISPLAY_CUSTOM_WEEKS:
			initialize1(numberOfWeeks);
			break;
		case DISPLAY_CUSTOM_MONTHS:
			initialize2(numberOfMonths);
			break;
		}
	}

	/**
	 * Initializes table model's data (for DISPLAY_MONTH and DISPLAY_YEAR).
	 */
	private void initialize0() {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		// Compute the first date.
		switch (displayMode) {
		case DISPLAY_MONTH:
			c1.setTime(displayEntireMonth ? DateUtils.firstDayOfMonth(referenceDate) : referenceDate);
			break;
		default:
			if (displayEntireYear) {
				c1.setTime(referenceDate);
				c1.set(Calendar.MONTH, c1.getMinimum(Calendar.MONTH));
				c1.set(Calendar.DATE, c1.getMinimum(Calendar.DATE));
			} else {
				c1.setTime(displayEntireMonth ? DateUtils.firstDayOfMonth(referenceDate) : referenceDate);
			}
			break;
		}

		// Compute the last date.
		c2.setTime(DateUtils.lastDayOfMonth(referenceDate));
		switch (displayMode) {
		case DISPLAY_MONTH:
			break;
		default:
			c2.set(Calendar.MONTH, c2.getMaximum(Calendar.MONTH));
			c2.set(Calendar.DATE, c2.getMaximum(Calendar.DATE));
			break;
		}

		// Initialize row values.
		initializeValues(c1.getTime(), getNumberOfWeeksToDisplay(c1.getTime(), c2.getTime()));
	}

	/**
	 * Initializes table model's data (for DISPLAY_CUSTOM_WEEKS).
	 */
	private void initialize1(int numberOfWeeks) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(displayEntireMonth ? DateUtils.firstDayOfMonth(referenceDate) : referenceDate);

		// Initialize row values.
		this.initializeValues(c1.getTime(), numberOfWeeks);
	}

	/**
	 * Initializes table model's data (for DISPLAY_CUSTOM_MONTHS).
	 */
	private void initialize2(int numberOfMonths) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(displayEntireMonth ? DateUtils.firstDayOfMonth(referenceDate) : referenceDate);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(referenceDate);
		c2.add(Calendar.MONTH, numberOfMonths - 1);
		c2.set(Calendar.DATE, c2.getMaximum(Calendar.DATE));

		// Initialize row values.
		this.initializeValues(c1.getTime(), getNumberOfWeeksToDisplay(c1.getTime(), c2.getTime()));
	}

	private void initializeValues(Date date, int weeks) {
		Calendar c = Calendar.getInstance();

		// Initialize row values.
		this.values = new long[weeks][7];

		Vector<DateLocation> temp = new Vector<DateLocation>();

		// We're gonna start with the date with time fields reset.
		c.setTime(DateUtils.resetTimeFields(date));
		// The first column corresponds to monday, so go back until the first MONDAY.
		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			c.add(Calendar.DATE, -1);
		}

		for (int i = 0; i < weeks; i++) {
			for (int j = 0; j < 7; j++) {
				values[i][j] = c.getTimeInMillis();
				if (c.get(Calendar.DATE) == 1) {
					temp.add(new DateLocation(values[i][j], i, j));
				}
				c.add(Calendar.DATE, 1);
			}
		}

		this.firstDayOfMonthLocations = temp.toArray(new DateLocation[temp.size()]);
	}

}