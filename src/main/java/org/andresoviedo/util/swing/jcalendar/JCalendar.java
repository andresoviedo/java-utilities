package org.andresoviedo.util.swing.jcalendar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andresoviedo.util.swing.jcalendar.resources.Resources;

/**
 * <code>JCalendar</code> is a component that lets the user pick up a date.
 * 
 */
public class JCalendar extends JPanel implements ItemListener, ChangeListener {

	/**
	 * The minimum year that can be selected.
	 */
	private static int MINIMUM_YEAR = 1980;

	/**
	 * The combo to let the user select a month.
	 */
	private JComboBox cboMonth;

	/**
	 * The spinner to let the user select a year.
	 */
	private JSpinner spYear;

	/**
	 * The spinner to let the user select the time.
	 */
	private JSpinner spTime;

	/**
	 * The calendar table to show the month.
	 */
	private JCalendarTable table;

	/**
	 * Indicates whether the user can edit the time part of the date or not.
	 */
	private boolean timeEditionAllowed;

	/**
	 * Creates a new calendar component initialized with the current date. The time part will be editable.
	 */
	public JCalendar() {
		this(new Date(), true);
	}

	/**
	 * Creates a new calendar component initialized with the current date.
	 * 
	 * @param timeEditionAllowed
	 *            indicates whether the user can edit the time part of the date or not. When <code>false</code>, the time editor won't be
	 *            visible.
	 */
	public JCalendar(boolean timeEditionAllowed) {
		this(new Date(), timeEditionAllowed);
	}

	/**
	 * Creates a new calendar component initialized with the specified date.
	 * 
	 * @param date
	 *            an arbitrary date.
	 * @param timeEditionAllowed
	 *            indicates whether the user can edit the time part of the date or not. When <code>false</code>, the time editor won't be
	 *            visible.
	 */
	public JCalendar(Date date, boolean timeEditionAllowed) {
		this.timeEditionAllowed = timeEditionAllowed;
		try {
			installComponents();
			installListeners();

			// Set the date.
			setDate(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Installs GUI components.
	 */
	private void installComponents() {
		setLayout(new BorderLayout(5, 5));
		add(createNorthPanel(), BorderLayout.NORTH);
		add(createCenterPanel(), BorderLayout.CENTER);
		if (timeEditionAllowed) {
			add(createSouthPanel(), BorderLayout.SOUTH);
		}
	}

	/**
	 * Installs listeners as needed.
	 */
	private void installListeners() {
		cboMonth.addItemListener(this);
		spYear.addChangeListener(this);
	}

	/**
	 * Creates a panel to select the month and the year.
	 * 
	 * @return a panel to select the month and the year.
	 */
	private JComponent createNorthPanel() {
		String[] months = new DateFormatSymbols().getMonths();

		// Create the combo box to select the month.
		cboMonth = new JComboBox();
		cboMonth.addItem(months[Calendar.JANUARY]);
		cboMonth.addItem(months[Calendar.FEBRUARY]);
		cboMonth.addItem(months[Calendar.MARCH]);
		cboMonth.addItem(months[Calendar.APRIL]);
		cboMonth.addItem(months[Calendar.MAY]);
		cboMonth.addItem(months[Calendar.JUNE]);
		cboMonth.addItem(months[Calendar.JULY]);
		cboMonth.addItem(months[Calendar.AUGUST]);
		cboMonth.addItem(months[Calendar.SEPTEMBER]);
		cboMonth.addItem(months[Calendar.OCTOBER]);
		cboMonth.addItem(months[Calendar.NOVEMBER]);
		cboMonth.addItem(months[Calendar.DECEMBER]);
		cboMonth.setPreferredSize(new Dimension(10, 20));

		// Create the spinner to select the year.
		int year = Calendar.getInstance().get(Calendar.YEAR);

		spYear = new JSpinner(new SpinnerNumberModel(year, MINIMUM_YEAR, year + 1, 1));
		spYear.setPreferredSize(new Dimension(10, 20));

		JFormattedTextField tf = ((JSpinner.DefaultEditor) spYear.getEditor()).getTextField();
		tf.setEditable(false);

		JPanel p = new JPanel(new GridLayout(1, 0, 5, 5));
		p.add(cboMonth);
		p.add(spYear);

		return p;
	}

	/**
	 * Creates a component with the calendar table inside.
	 * 
	 * @return a component with the calendar table inside.
	 */
	private JComponent createCenterPanel() {
		table = new JCalendarTable(new CalendarTableModel());
		table.setDisplayRowHeaderView(false);
		table.getCalendarModel().setNumberOfWeeks(6);
		table.getCalendarModel().setDisplayMode(CalendarTableModel.DISPLAY_CUSTOM_WEEKS);

		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(UIManager.getBorder("ComboBox.border"));
		p.add(table.getColumnHeaderView(), BorderLayout.NORTH);
		p.add(table, BorderLayout.CENTER);

		// Change the preferred width.
		Dimension dim = p.getPreferredSize();
		dim.width = 200;
		p.setPreferredSize(dim);

		return p;
	}

	/**
	 * Creates a component to select the time.
	 * 
	 * @return a component to select the time.
	 */
	private JComponent createSouthPanel() {
		spTime = new JSpinner(new SpinnerDateModel());
		spTime.setPreferredSize(new Dimension(10, 20));

		JSpinner.DateEditor editor = new JSpinner.DateEditor(spTime, "HH:mm:ss");
		editor.getTextField().setEditable(false);

		spTime.setEditor(editor);

		JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel(Resources.getString(Resources.LABEL_TIME)), BorderLayout.WEST);
		p.add(spTime, BorderLayout.CENTER);

		return p;
	}

	/**
	 * Returns the selected date. May be <code>null</code> if no date is selected.
	 * 
	 * @return the selected date.
	 */
	public Date getDate() {
		if (!table.isDateSelected()) {
			return null;
		}
		Calendar c1 = Calendar.getInstance();
		c1.setTime(table.getSelectedDate());

		if (timeEditionAllowed) {
			Calendar c2 = Calendar.getInstance();
			c2.setTime((Date) spTime.getValue());

			c1.set(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR_OF_DAY));
			c1.set(Calendar.MINUTE, c2.get(Calendar.MINUTE));
			c1.set(Calendar.SECOND, c2.get(Calendar.SECOND));
			c1.set(Calendar.MILLISECOND, c2.getMinimum(Calendar.MILLISECOND));
		}

		return c1.getTime();
	}

	/**
	 * Sets the selected date. If the specified date is <code>null</code>, this method does nothing.
	 * 
	 * @param date
	 *            an arbitrary date.
	 */
	public void setDate(Date date) {
		if (date == null) {
			return;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		spYear.setValue(new Integer(c.get(Calendar.YEAR)));
		cboMonth.setSelectedIndex(c.get(Calendar.MONTH));
		if (timeEditionAllowed) {
			spTime.setValue(date);
		}

		// The reference date for calendar table model should be the first day of month.
		c.set(Calendar.DATE, c.getMinimum(Calendar.DATE));

		// Select the current date.
		table.getCalendarModel().setReferenceDate(c.getTime());
		table.setSelectedDate(date);
	}

	/*
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			// Get the selected date to preserve the day of month.
			Calendar c = Calendar.getInstance();
			int dayOfMonth = -1;
			if (table.isDateSelected()) {
				c.setTime(table.getSelectedDate());
				dayOfMonth = c.get(Calendar.DATE);
			}

			// Perform necessary changes.
			c.setTime(table.getCalendarModel().getReferenceDate());
			c.set(Calendar.MONTH, cboMonth.getSelectedIndex());
			table.getCalendarModel().setReferenceDate(c.getTime());

			// Restore the selected date.
			if (dayOfMonth != -1) {
				c.set(Calendar.DATE, Math.min(dayOfMonth, c.getActualMaximum(Calendar.DATE)));
				table.setSelectedDate(c.getTime());
			}
		}
	}

	/*
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		// Get the selected date to preserve the day of month.
		Calendar c = Calendar.getInstance();
		int dayOfMonth = -1;
		if (table.isDateSelected()) {
			c.setTime(table.getSelectedDate());
			dayOfMonth = c.get(Calendar.DATE);
		}

		c.setTime(table.getCalendarModel().getReferenceDate());
		c.set(Calendar.YEAR, ((Integer) spYear.getValue()).intValue());
		table.getCalendarModel().setReferenceDate(c.getTime());

		// Restore the selected date.
		if (dayOfMonth != -1) {
			c.set(Calendar.DATE, Math.min(dayOfMonth, c.getActualMaximum(Calendar.DATE)));
			table.setSelectedDate(c.getTime());
		}
	}

	/**
	 * Shows a dialog with a <code>JCalendar</code> component inside and initializes it with the specified date. This method uses a plain
	 * <code>JOptionPane</code> component with the <code>JOptionPane.OK_CANCEL</code> option. The time part will be editable.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is displayed.
	 * @param title
	 *            the title string for the dialog.
	 * @param date
	 *            the date to initialize the calendar.
	 * @return user's selected date. May return <code>null</code> if the user cancelled the selection.
	 */
	public static Date showDialog(Component parentComponent, String title, Date date) {
		return showDialog(parentComponent, title, date, true);
	}

	/**
	 * Shows a dialog with a <code>JCalendar</code> component inside and initializes it with the specified date. This method uses a plain
	 * <code>JOptionPane</code> component with the <code>JOptionPane.OK_CANCEL</code> option.
	 * 
	 * @param parentComponent
	 *            determines the <code>Frame</code> in which the dialog is displayed.
	 * @param title
	 *            the title string for the dialog.
	 * @param date
	 *            the date to initialize the calendar.
	 * @param timeEditionAllowed
	 *            indicates whether the user can edit the time part of the date or not. When <code>false</code>, the time editor won't be
	 *            visible.
	 * @return user's selected date. May return <code>null</code> if the user cancelled the selection.
	 */
	public static Date showDialog(Component parentComponent, String title, Date date, boolean timeEditionAllowed) {
		JCalendar calendar = new JCalendar(date, timeEditionAllowed);

		if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(parentComponent), calendar, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			return calendar.getDate();
		}
		return null;
	}

}