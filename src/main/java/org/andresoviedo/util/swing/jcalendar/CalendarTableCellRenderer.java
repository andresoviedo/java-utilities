package org.andresoviedo.util.swing.jcalendar;

import java.awt.Component;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.andresoviedo.util.date.DateUtils;

/**
 * Calendar table cell renderer.
 * 
 */
class CalendarTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * The calendar used to render date values.
	 */
	private Calendar calendar = Calendar.getInstance();

	/**
	 * The associated calendar table.
	 */
	private JCalendarTable calendarTable;

	/**
	 * Creates a new calendar cell renderer.
	 * 
	 * @param calendarTable
	 *            the associated calendar table.
	 */
	public CalendarTableCellRenderer(JCalendarTable calendarTable) {
		this.calendarTable = calendarTable;
		this.setBorder(null);
		this.setHorizontalAlignment(JLabel.CENTER);
	}

	/*
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean,
	 * int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		// Get the date.
		Date date = new Date(((Long) value).longValue());
		CalendarDateLabelDecorator decorator = calendarTable.getDecorator(date);

		// If the day is selected, keep the selection.
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			// Mark the current date in a special manner.
			if (DateUtils.dateFieldsEqual(date, new Date())) {
				setBackground(calendarTable.getCurrentDateBackground());
				setForeground(calendarTable.getCurrentDateForeground());
			} else if ((decorator != null) && decorator.isEnabled()) {
				setBackground(decorator.getBackground());
				setForeground(decorator.getForeground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
		}

		setEnabled((decorator != null) ? decorator.isEnabled() : true);
		setToolTipText((decorator != null) ? decorator.getDescription() : null);

		return this;
	}

	/*
	 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
	 */
	protected void setValue(Object value) {
		calendar.setTimeInMillis(((Long) value).longValue());
		setText(String.valueOf(calendar.get(Calendar.DATE)));
	}

}