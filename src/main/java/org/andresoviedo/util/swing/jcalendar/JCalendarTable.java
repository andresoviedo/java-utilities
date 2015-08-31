package org.andresoviedo.util.swing.jcalendar;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableModel;

import org.andresoviedo.util.date.DateUtils;
import org.andresoviedo.util.swing.jcalendar.CalendarTableModel.DateLocation;

/**
 * A calendar table component.
 * 
 */
public class JCalendarTable extends JTable {

	private class CalendarTableListener extends MouseAdapter implements ListSelectionListener {

		private int originalToolTipDismissDelay;

		private int originalToolTipInitialDelay;
		private int originalToolTipReshowDelay;
		private boolean ownToolTipDelaysActive;

		@Override
		public void mouseEntered(MouseEvent e) {
			if (!ownToolTipDelaysActive) {
				ToolTipManager ttm = ToolTipManager.sharedInstance();

				originalToolTipInitialDelay = ttm.getInitialDelay();
				originalToolTipReshowDelay = ttm.getReshowDelay();
				originalToolTipDismissDelay = ttm.getDismissDelay();

				ttm.setInitialDelay(0);
				ttm.setReshowDelay(0);
				ttm.setDismissDelay(Integer.MAX_VALUE);

				ownToolTipDelaysActive = true;
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (ownToolTipDelaysActive) {
				ToolTipManager ttm = ToolTipManager.sharedInstance();
				ttm.setInitialDelay(originalToolTipInitialDelay);
				ttm.setReshowDelay(originalToolTipReshowDelay);
				ttm.setDismissDelay(originalToolTipDismissDelay);

				ownToolTipDelaysActive = false;
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			repaint();
		}

	}

	/**
	 * The calendar table model.
	 */
	private CalendarTableModel calendarModel;

	/**
	 * The calendar column header view (lazily instantiated).
	 */
	private CalendarColumnHeaderView columnHeaderView;

	/**
	 * The color the cell renderer should use as the background to paint the cell representing the current date.
	 */
	private Color currentDateBackground = UIManager.getColor("activeCaption");// Color.darkGray;

	/**
	 * The color the cell renderer should use as the foreground to paint the cell representing the current date.
	 */
	private Color currentDateForeground = UIManager.getColor("activeCaptionText");// Color.white;

	/**
	 * The list of decorators.
	 */
	private List<CalendarDateLabelDecorator> decorators = new Vector<CalendarDateLabelDecorator>();

	/**
	 * A flag indicating that the column header view should be displayed instead of the table header when the table is inside a scroll pane.
	 */
	private boolean displayColumnHeaderView = true;

	/**
	 * A flag indicating that the row header view should be displayed when the table is inside a scroll pane.
	 */
	private boolean displayRowHeaderView = true;

	/**
	 * The color used to draw month delimiters.
	 */
	private Color monthDelimiterColor = UIManager.getColor("activeCaption");// Color.darkGray;

	/**
	 * The calendar row header view (lazily instantiated).
	 */
	private CalendarRowHeaderView rowHeaderView;

	/**
	 * Creates a new calendar table.
	 */
	public JCalendarTable() {
		this(null);
	}

	/**
	 * Creates a new calendar table.
	 * 
	 * @param model
	 *            a calendar table model.
	 */
	public JCalendarTable(CalendarTableModel model) {
		super(model);

		// Keep a reference to the model (this is done this way just in case the passed in model was null).
		// this.model = (CalendarTableModel) getModel();

		// Allow the selection of cells (each cell represents a particular day).
		this.setCellSelectionEnabled(true);
		// Only one day can be selected at a time.
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Set the renderer we will use to paint each cell.
		this.setDefaultRenderer(Object.class, new CalendarTableCellRenderer(this));

		// Reordering and resizing are not allowed.
		this.getTableHeader().setResizingAllowed(false);
		this.getTableHeader().setReorderingAllowed(false);

		// Set the listeners.
		CalendarTableListener l = new CalendarTableListener();

		this.addMouseListener(l);
		this.getSelectionModel().addListSelectionListener(l);
		this.getColumnModel().getSelectionModel().addListSelectionListener(l);
	}

	/**
	 * Adds a specific label decorator.
	 * 
	 * @param decorator
	 *            the decorator to add.
	 */
	public void addDecorator(CalendarDateLabelDecorator decorator) {
		if (decorator != null) {
			synchronized (decorators) {
				decorators.add(decorator);
			}
			repaint();
		}
	}

	/**
	 * Returns the calendar model. This is a convinience method to avoid writing <code>(CalendarTableModel) getModel()</code>.
	 * 
	 * @return the calendar model.
	 */
	public CalendarTableModel getCalendarModel() {
		return calendarModel;
	}

	/**
	 * Returns the column header view component. This component may have been set as the column header view of the scroll pane holding the
	 * table.
	 * 
	 * @return the column header view component.
	 */
	public CalendarColumnHeaderView getColumnHeaderView() {
		if (columnHeaderView == null) {
			columnHeaderView = new CalendarColumnHeaderView(this);
		}
		return columnHeaderView;
	}

	/**
	 * Returns the color the cell renderer should use as the background to paint the cell representing the current date.
	 * 
	 * @return the color the cell renderer should use as the background to paint the cell representing the current date.
	 */
	public Color getCurrentDateBackground() {
		return currentDateBackground;
	}

	/**
	 * Returns the color the cell renderer should use as the foreground to paint the cell representing the current date.
	 * 
	 * @return the color the cell renderer should use as the foreground to paint the cell representing the current date.
	 */
	public Color getCurrentDateForeground() {
		return currentDateForeground;
	}

	/**
	 * Returns the decorator associated to the specified date (may be <code>null</code>).
	 * 
	 * @param date
	 *            the date.
	 * @return the decorator associated to the specified date.
	 */
	public CalendarDateLabelDecorator getDecorator(Date date) {
		synchronized (decorators) {
			for (CalendarDateLabelDecorator decorator : decorators) {
				if (decorator.matches(date)) {
					return decorator;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the color used to paint month delimiters.
	 * 
	 * @return the color used to paint month delimiters.
	 */
	public Color getMonthDelimiterColor() {
		return monthDelimiterColor;
	}

	/**
	 * Returns the row header view component. This component may have been set as the row header view of the scroll pane holding the table.
	 * 
	 * @return the row header view component.
	 */
	public CalendarRowHeaderView getRowHeaderView() {
		if (rowHeaderView == null) {
			rowHeaderView = new CalendarRowHeaderView(this);
		}
		return rowHeaderView;
	}

	/**
	 * Returns the selected date.
	 * 
	 * @return the selected date.
	 */
	public Date getSelectedDate() {
		int col = getSelectedColumn();
		int row = getSelectedRow();
		if ((col != -1) && (row != -1)) {
			return calendarModel.getDateValueAt(row, col);
		}
		return null;
	}

	/**
	 * Returns whether the user has selected a date.
	 * 
	 * @return <code>true</code> if a date is selected, <code>false</code> otherwise.
	 */
	public boolean isDateSelected() {
		return (getSelectedColumn() != -1) && (getSelectedRow() != -1);
	}

	/**
	 * Returns the value of the display column header view flag.
	 * 
	 * @return the value of the display column header view flag.
	 */
	public boolean isDisplayColumnHeaderView() {
		return displayColumnHeaderView;
	}

	/**
	 * Returns the value of the display row header view flag.
	 * 
	 * @return the value of the display row header view flag.
	 */
	public boolean isDisplayRowHeaderView() {
		return displayRowHeaderView;
	}

	/**
	 * Removes all decorators from the list.
	 */
	public void removeDecorators() {
		synchronized (decorators) {
			if (decorators.size() > 0) {
				decorators.clear();
			}
		}
		repaint();
	}

	/**
	 * Sets the color the cell renderer should use as the background to paint the cell representing the current date.
	 * 
	 * @param currentDateBackground
	 *            the desired color.
	 */
	public void setCurrentDateBackground(Color currentDateBackground) {
		this.currentDateBackground = currentDateBackground;
		this.repaint();
	}

	/**
	 * Sets the color the cell renderer should use as the foreground to paint the cell representing the current date.
	 * 
	 * @param currentDateForeground
	 *            the desired color.
	 */
	public void setCurrentDateForeground(Color currentDateForeground) {
		this.currentDateForeground = currentDateForeground;
		this.repaint();
	}

	/**
	 * Sets whether the column header view has to be displayed or not. If the table is not the main view of a scrollpane's viewport, this
	 * method does nothing.
	 * 
	 * @param displayColumnHeaderView
	 *            <code>true</code> if the column header view has to be displayed, <code>false</code> otherwise.
	 */
	public void setDisplayColumnHeaderView(boolean displayColumnHeaderView) {
		if (this.displayColumnHeaderView == displayColumnHeaderView) {
			return;
		}
		this.displayColumnHeaderView = displayColumnHeaderView;
		JScrollPane scrollPane = getScrollPane();
		if (scrollPane != null) {
			if (displayColumnHeaderView) {
				scrollPane.setColumnHeaderView(getColumnHeaderView());
			} else {
				// TODO: set the table header as the column header view?
				scrollPane.setColumnHeaderView(null);
			}
		}
	}

	/**
	 * Sets whether the row header view has to be displayed or not. If the table is not the main view of a scrollpane's viewport, this
	 * method does nothing.
	 * 
	 * @param displayRowHeaderView
	 *            <code>true</code> if the row header view has to be displayed, <code>false</code> otherwise.
	 */
	public void setDisplayRowHeaderView(boolean displayRowHeaderView) {
		if (this.displayRowHeaderView == displayRowHeaderView) {
			return;
		}
		this.displayRowHeaderView = displayRowHeaderView;

		JScrollPane scrollPane = getScrollPane();
		if (scrollPane != null) {
			if (displayRowHeaderView) {
				scrollPane.setRowHeaderView(getRowHeaderView());
			} else {
				scrollPane.setRowHeaderView(null);
			}
		}
	}

	/**
	 * Overwritten to disallow setting other table models rather than a calendar table model and to keep a reference to the current calendar
	 * table model.
	 * 
	 * @see javax.swing.JTable#setModel(javax.swing.table.TableModel)
	 */
	public void setModel(TableModel dataModel) {
		if (!(dataModel instanceof CalendarTableModel)) {
			throw new IllegalArgumentException("Model is not an instance of CalendarTableModel.");
		}
		this.calendarModel = (CalendarTableModel) dataModel;
		super.setModel(dataModel);
	}

	/**
	 * Sets the color used to paint month delimiters.
	 * 
	 * @param monthDelimiterColor
	 *            the color used to paint month delimiters.
	 */
	public void setMonthDelimiterColor(Color monthDelimiterColor) {
		this.monthDelimiterColor = monthDelimiterColor;
		this.repaint();
	}

	/**
	 * Sets the selected date. If <code>date</code> is null, this method does nothing.
	 * 
	 * @param date
	 *            an arbitrary date.
	 */
	public void setSelectedDate(Date date) {
		if (date == null) {
			return;
		}
		DateLocation loc = calendarModel.getDateLocation(date);
		if (loc != null) {
			setColumnSelectionInterval(loc.getColumn(), loc.getColumn());
			setRowSelectionInterval(loc.getRow(), loc.getRow());
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		// Preserve custom settings.
		Color c = getMonthDelimiterColor();
		if ((c == null) || (c instanceof UIResource)) {
			setMonthDelimiterColor(UIManager.getColor("activeCaption"));
		}
		c = getCurrentDateBackground();
		if ((c == null) || (c instanceof UIResource)) {
			setCurrentDateBackground(UIManager.getColor("activeCaption"));
		}
		c = getCurrentDateForeground();
		if ((c == null) || (c instanceof UIResource)) {
			setCurrentDateForeground(UIManager.getColor("activeCaptionText"));
		}
	}

	/**
	 * Overwritten to set custom row and column header views in the scroll pane.
	 * 
	 * @see javax.swing.JTable#configureEnclosingScrollPane()
	 */
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();

		JScrollPane scrollPane = getScrollPane();
		if (scrollPane != null) {
			if (isDisplayColumnHeaderView()) {
				scrollPane.setColumnHeaderView(getColumnHeaderView());
			}
			if (isDisplayRowHeaderView()) {
				scrollPane.setRowHeaderView(getRowHeaderView());
			}
		}
	}

	/**
	 * Overwritten to return a calendar table model. This is useful when invoking the constructor with a <code>null</code> table model.
	 * 
	 * @see javax.swing.JTable#createDefaultDataModel()
	 */
	protected TableModel createDefaultDataModel() {
		return new CalendarTableModel();
	}

	/**
	 * Returns the scrollpane we're currently in if we're the view of its viewport. Otherwise, returns <code>null</code>.
	 * 
	 * @return the scrollpane we're currently in if we're the view of its viewport. Otherwise, returns <code>null</code>.
	 */
	protected JScrollPane getScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				// Make certain we are the viewPort's view and not, for example, the rowHeaderView of the scrollPane.
				JViewport viewport = scrollPane.getViewport();
				if ((viewport != null) && (viewport.getView() == this)) {
					return scrollPane;
				}
			}
		}
		return null;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Custom painting of borders.
		Rectangle r1, r2;

		g.setColor(monthDelimiterColor);

		DateLocation[] array = calendarModel.getFirstDayOfMonthLocations();
		for (int i = 0; i < array.length; i++) {
			r1 = getCellRect(array[i].getRow(), array[i].getColumn(), false);

			g.drawLine(0, r1.y + r1.height, r1.x - 1, r1.y + r1.height);
			g.drawLine(r1.x - 1, r1.y + r1.height, r1.x - 1, r1.y - 1);
			g.drawLine(r1.x, r1.y - 1, getWidth(), r1.y - 1);
		}

		// Highlight the month.
		// Date date = (calendarModel.getDisplayMode() == CalendarTableModel.DISPLAY_MONTH) ? calendarModel.getDate() : getSelectedDate();
		Date date = getSelectedDate();
		if (date == null) {
			return;
		}

		DateLocation loc1 = calendarModel.getFirstDayOfMonthLocation(date);
		DateLocation loc2 = calendarModel.getFirstDayOfMonthLocation(DateUtils.add(date, Calendar.MONTH, 1));
		if ((loc1 == null) && (loc2 == null)) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g;
		Composite saved = g2.getComposite();

		g2.setColor(getSelectionBackground());
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

		if ((loc1 != null) && (loc2 == null)) {
			// Case 1: the last row.
			r1 = getCellRect(loc1.getRow(), loc1.getColumn(), false);
			g2.fillRect(0, r1.y + r1.height, getWidth(), getHeight() - r1.y - r1.height);
			g2.fillRect(r1.x, r1.y, getWidth() - r1.x, r1.height);
		} else if ((loc1 == null) && (loc2 != null)) {
			// Case 2: the first row.
			r1 = getCellRect(loc2.getRow(), loc2.getColumn(), false);
			// System.out.println(r1);
			g2.fillRect(0, 0, getWidth(), r1.y);
			g2.fillRect(0, 0, r1.x, r1.y + r1.height);
		} else {
			// Case 3:
			r1 = getCellRect(loc1.getRow(), loc1.getColumn(), false);
			r2 = getCellRect(loc2.getRow(), loc2.getColumn(), false);
			// Upper rect.
			g2.fillRect(r1.x, r1.y, getWidth() - r1.x, r1.height);
			// Middle rect.
			g2.fillRect(0, r1.y + r1.height, getWidth(), r2.y - r1.y - r1.height);
			// Bottom rect.
			g2.fillRect(0, r2.y, r2.x, r2.height);
		}

		g2.setComposite(saved);
	}

}