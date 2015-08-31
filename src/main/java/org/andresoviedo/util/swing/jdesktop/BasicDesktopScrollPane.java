package org.andresoviedo.util.swing.jdesktop;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * A custom scrollpane to hold a basic desktop pane.
 * 
 */
public class BasicDesktopScrollPane extends JScrollPane {

	/**
	 * Desktop pane used as the view.
	 */
	private BasicDesktopPane desktopPane;

	/**
	 * Creates a new desktop scroll pane, using the specified desktop pane as the view.
	 * 
	 * @param desktopPane
	 *            the desktop pane.
	 */
	public BasicDesktopScrollPane(BasicDesktopPane desktopPane) {
		this.desktopPane = desktopPane;
		setViewportView(desktopPane);

		getHorizontalScrollBar().setUnitIncrement(50);
		getHorizontalScrollBar().setBlockIncrement(50);

		getVerticalScrollBar().setUnitIncrement(50);
		getVerticalScrollBar().setBlockIncrement(50);
	}

	/**
	 * Resizes the view based on visible internal frames.
	 */
	public void resizeDesktop() {
		if (getParent() == null) {
			return;
		}

		// Loop through all the internal frames and make sure that none is off screen, and if so, add scroll bars.
		Rectangle viewP = getViewport().getViewRect();

		int minX = viewP.x;
		int minY = viewP.y;
		int maxX = viewP.width + viewP.x;
		int maxY = viewP.height + viewP.y;

		// Determine the min/max extents of all internal frames.
		JInternalFrame f = null;
		JInternalFrame[] frames = desktopPane.getAllFrames();

		for (int i = 0; i < frames.length; i++) {
			f = frames[i];

			// Get minimum X.
			if (f.getX() < minX) {
				minX = f.getX();
			}

			// Get the maximum X.
			if ((f.getX() + f.getWidth()) > maxX) {
				maxX = f.getX() + f.getWidth();
			}

			// Get minimum Y.
			if (f.getY() < minY) {
				minY = f.getY();
			}

			// Get the maximum Y.
			if ((f.getY() + f.getHeight()) > maxY) {
				maxY = f.getY() + f.getHeight();
			}
		}

		// Don't update the viewport while we move everything (otherwise desktop looks 'bouncy').
		setVisible(false);

		if ((minX != 0) || (minY != 0)) {
			// We have to scroll it to the right or up the amount that it's off screen.
			// Before scroll, move every component to the right / down by that amount.
			for (int i = 0; i < frames.length; i++) {
				f = frames[i];
				f.setLocation(f.getX() - minX, f.getY() - minY);
				/*
				 * if (f.isIcon()) { JInternalFrame.JDesktopIcon di = f.getDesktopIcon(); di.setLocation(di.getX() - minX, di.getY() -
				 * minY); }
				 */
			}

			// We have to scroll (set the viewport) to the right or up the amount that it's off screen.
			JViewport view = getViewport();
			view.setViewSize(new Dimension((maxX - minX), (maxY - minY)));
			view.setViewPosition(new Point((viewP.x - minX), (viewP.y - minY)));
			// setViewport(view);
		}

		// Resize the desktop pane.
		setDesktopSize(new Dimension(maxX - minX, maxY - minY));
		setVisible(true);
	}

	/**
	 * Sets the preferred size of the desktop pane.
	 * 
	 * @param dim
	 *            the new size of the desktop pane.
	 */
	public void setDesktopSize(Dimension dim) {
		desktopPane.setPreferredSize(dim);
		desktopPane.revalidate();
	}

}