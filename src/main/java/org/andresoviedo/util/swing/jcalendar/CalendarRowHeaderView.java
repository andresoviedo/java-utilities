package org.andresoviedo.util.swing.jcalendar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.Calendar;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.UIResource;

import org.andresoviedo.util.date.DateUtils;
import org.andresoviedo.util.swing.SwingUtils;
import org.andresoviedo.util.swing.jcalendar.CalendarTableModel.DateLocation;

/**
 * The calendar row header view component is a component intended to be used as a row header view for a <code>JScrollPane</code> holding a
 * <code>CalendarTable</code>.
 * 
 */
public class CalendarRowHeaderView extends JComponent implements ListSelectionListener {

	/**
	 * A calendar object used in paintComponent() to avoid creating it every time the component is repainted.
	 */
	private Calendar c = Calendar.getInstance();

	/**
	 * The associated calendar table.
	 */
	private JCalendarTable table;

	/**
	 * The selection background. This color is used to paint the selected month based on the selected date.
	 */
	private Color selectionBackground;

	/**
	 * The selection foreground. This color is used to paint the selected month string based on the selected date.
	 */
	private Color selectionForeground;

	/**
	 * The date pattern used to draw months.
	 */
	private String datePattern = "MMM yy";

	/**
	 * A 90 degrees rotated version of the font we're using.
	 */
	private Font rotatedFont;

	/**
	 * The preferred width.
	 */
	private int preferredWidth;

	/**
	 * Creates a new calendar row header view.
	 * 
	 * @param table
	 *            the associated calendar table component.
	 */
	public CalendarRowHeaderView(JCalendarTable table) {
		this.table = table;
		this.table.getSelectionModel().addListSelectionListener(this);
		this.table.getColumnModel().getSelectionModel().addListSelectionListener(this);
		this.table.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				setPreferredWidth(getPreferredWidth());
			}
		});
		this.setFont(UIManager.getFont("Label.font"));
		this.setPreferredWidth(15);
		this.setBackground(UIManager.getColor("activeCaption"));// Color.darkGray);
		this.setForeground(UIManager.getColor("activeCaptionText"));// Color.white);
		this.setSelectionBackground(UIManager.getColor("Table.selectionBackground"));// Color.orange);
		this.setSelectionForeground(UIManager.getColor("Table.selectionForeground"));// Color.black);

		this.rotatedFont = getFont().deriveFont(AffineTransform.getRotateInstance(Math.toRadians(90)));
	}

	/**
	 * Returns the date pattern.
	 * 
	 * @return the date pattern.
	 */
	public String getDatePattern() {
		return datePattern;
	}

	/**
	 * Sets the date pattern.
	 * 
	 * @param datePattern
	 *            the new date pattern.
	 */
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	/**
	 * Returns the tab selection background.
	 * 
	 * @return the tab selection background.
	 */
	public Color getSelectionBackground() {
		return selectionBackground;
	}

	/**
	 * Sets the tab selection background.
	 * 
	 * @param selectionBackground
	 *            the new selection background.
	 */
	public void setSelectionBackground(Color selectionBackground) {
		if (selectionBackground != null) {
			this.selectionBackground = selectionBackground;
			this.repaint();
		}
	}

	/**
	 * Returns the tab selection foreground.
	 * 
	 * @return the tab selection foreground.
	 */
	public Color getSelectionForeground() {
		return selectionForeground;
	}

	/**
	 * Sets the tab selection foreground.
	 * 
	 * @param selectionForeground
	 *            the new selection foreground.
	 */
	public void setSelectionForeground(Color selectionForeground) {
		if (selectionForeground != null) {
			this.selectionForeground = selectionForeground;
			this.repaint();
		}
	}

	/**
	 * Returns the preferred width.
	 * 
	 * @return the preferred width.
	 */
	public int getPreferredWidth() {
		return preferredWidth;
	}

	/**
	 * Sets the preferred width of the component.
	 * 
	 * @param preferredWidth
	 *            the preferred width.
	 */
	public void setPreferredWidth(int preferredWidth) {
		if (preferredWidth < 0) {
			throw new IllegalArgumentException("The preferred width cannot be less than 0.");
		}
		this.preferredWidth = preferredWidth;
		this.setPreferredSize(new Dimension(preferredWidth, table.getHeight()));
		this.revalidate();
	}

	/*
	 * @see javax.swing.JComponent#updateUI()
	 */
	public void updateUI() {
		super.updateUI();
		// Will preserve custom colors.
		LookAndFeel.installColors(this, "activeCaption", "activeCaptionText");
		// Set selection colors preserving custom ones.
		Color c = getSelectionBackground();
		if ((c == null) || (c instanceof UIResource)) {
			setSelectionBackground(UIManager.getColor("Table.selectionBackground"));
		}
		c = getSelectionForeground();
		if ((c == null) || (c instanceof UIResource)) {
			setSelectionForeground(UIManager.getColor("Table.selectionForeground"));
		}
	}

	/*
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		Rectangle clip = g.getClipBounds();

		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(rotatedFont);

		// Fill clipping area using the background color.
		g2.setColor(getBackground());
		g2.fillRect(clip.x, clip.y, clip.width, clip.height);

		FontMetrics fm = SwingUtils.getFontMetrics(getFont(), g);

		// Paint the months.
		Rectangle r1, r2;
		boolean selected;
		int x, y, w, h;
		int y1;
		int y2;

		Calendar c2 = null;
		if (table.isDateSelected()) {
			c2 = Calendar.getInstance();
			c2.setTime(table.getSelectedDate());
		}

		DateLocation[] array = table.getCalendarModel().getFirstDayOfMonthLocations();
		for (int i = 0; i < array.length; i++) {
			// Get cell bounds.
			r1 = table.getCellRect(array[i].getRow(), array[i].getColumn(), false);
			if (i < array.length - 1) {
				r2 = table.getCellRect(array[i + 1].getRow(), array[i + 1].getColumn(), false);
			} else {
				r2 = table.getCellRect(table.getRowCount() - 1, table.getColumnCount() - 1, false);
			}

			// Compute tab bounds.
			y1 = (r1.x == 0) ? r1.y : r1.y + r1.height;
			y2 = (r2.x == 0) ? r2.y : r2.y + r2.height;

			// Set the values.
			x = 0;
			y = y1;
			w = getWidth() - 1;
			h = y2 - y1;

			// Limit the drawing area.
			g2.setClip(clip);
			g2.clipRect(x, y, w, h);

			// Get the corresponding month.
			c.setTimeInMillis(array[i].getDate());

			// Do we have to draw the month as selected?
			selected = (c2 != null) && (c.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) && (c.get(Calendar.YEAR) == c2.get(Calendar.YEAR));

			// Draw the tab.
			g2.setColor(selected ? getSelectionBackground() : getBackground());
			g2.fillRect(x, y, w, h);

			// Draw the string.
			String s = DateUtils.formatDate(c.getTime(), datePattern);

			g2.setColor(selected ? getSelectionForeground() : getForeground());
			g2.drawString(s, x + fm.getDescent() + ((w - fm.getAscent() - fm.getDescent()) / 2), y + ((h - fm.stringWidth(s)) / 2));
		}
	}

	/*
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		repaint();
	}

}