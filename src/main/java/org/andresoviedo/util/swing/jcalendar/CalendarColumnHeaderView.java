package org.andresoviedo.util.swing.jcalendar;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 * The calendar column header view component is a component intended to be used as a column header view for a <code>JScrollPane</code>
 * holding a <code>CalendarTable</code> instead of its table header. Since resizing and reordering is not allowed, not using the table
 * header as the column header view is not a problem.
 * 
 */
public class CalendarColumnHeaderView extends JComponent {

	/**
	 * The associated calendar table.
	 */
	private JCalendarTable table;

	/**
	 * The preferred height.
	 */
	private int preferredHeight;

	/**
	 * Creates a new calendar row header view.
	 * 
	 * @param table
	 *            the associated calendar table component.
	 */
	public CalendarColumnHeaderView(JCalendarTable table) {
		this.table = table;
		this.table.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				setPreferredHeight(getPreferredHeight());
			}
		});
		this.setFont(UIManager.getFont("Label.font"));
		this.setPreferredHeight(15);
		this.setBackground(UIManager.getColor("activeCaption"));// Color.darkGray);
		this.setForeground(UIManager.getColor("activeCaptionText"));// Color.white);
	}

	/**
	 * Returns the preferred height.
	 * 
	 * @return the preferred height.
	 */
	public int getPreferredHeight() {
		return preferredHeight;
	}

	/**
	 * Sets the preferred height of the component.
	 * 
	 * @param preferredHeight
	 *            the preferred height.
	 */
	public void setPreferredHeight(int preferredHeight) {
		if (preferredHeight < 0) {
			throw new IllegalArgumentException("The preferred height cannot be less than 0.");
		}
		this.preferredHeight = preferredHeight;
		this.setPreferredSize(new Dimension(table.getWidth(), preferredHeight));
		this.revalidate();
	}

	/*
	 * @see javax.swing.JComponent#updateUI()
	 */
	public void updateUI() {
		super.updateUI();
		// Will preserve custom colors.
		LookAndFeel.installColors(this, "activeCaption", "activeCaptionText");
	}

	/*
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		Rectangle clip = g.getClipBounds();
		FontMetrics fm = g.getFontMetrics(getFont());

		// Fill clipping area with tab background.
		g.setColor(getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		// Paint each cell.
		g.setColor(getForeground());

		Rectangle r;
		int x, y, w, h;

		int cols = table.getColumnCount();
		for (int i = 0; i < cols; i++) {
			// Get cell bounds.
			r = table.getCellRect(0, i, false);

			// Set the values.
			x = r.x;
			y = 0;
			w = r.width;
			h = getHeight();

			// Limit the drawing area.
			g.setClip(clip);
			g.clipRect(x, y, w, h);

			// Draw the string.
			String s = table.getColumnName(i);
			g.drawString(s, x + (w - fm.stringWidth(s)) / 2, y + (h - fm.getAscent() - fm.getDescent()) / 2 + fm.getAscent());
		}
	}

}
