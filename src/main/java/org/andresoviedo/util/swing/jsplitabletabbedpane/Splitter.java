package org.andresoviedo.util.swing.jsplitabletabbedpane;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import org.andresoviedo.util.swing.jsplitabletabbedpane.resources.Resources;


/**
 * Splitter.
 * 

 */
class Splitter extends MouseInputAdapter implements ContainerListener, ActionListener {

	/**
	 * The minimum distance to the edges to detect a move.
	 */
	private static final int MIN_DISTANCE = 20;

	/**
	 * A reference to the origin tabbed pane (where the dragged tab will be removed from).
	 */
	private JTabbedPane tpSource;

	/**
	 * A reference to the destination tabbed pane (where the dragged tab will be moved to).
	 */
	private JTabbedPane tpDestination;

	/**
	 * The index of the dragged tab, in the source tabbed pane.
	 */
	private int sourceIndex = -1;

	/**
	 * The destination index (where the dragged tab will be moved to).
	 */
	private int destinationIndex = -1;

	/**
	 * Rectangle to highlight the destination zone.
	 */
	private Rectangle rectangle = null;

	/**
	 * The new position where the tab is to be dragged (one of NONE, TOP, BOTTOM, LEFT, RIGHT).
	 */
	private int newPosition = SplitterConstants.NO_SPLIT;

	/**
	 * The component holding the tabbed panes.
	 */
	private JSplitableTabbedPane topPanel;

	/**
	 * The popup menu to be show when the user right clicks on a tab.
	 */
	private JPopupMenu tabbedPanePopup;

	private JMenuItem miClose;
	private JMenuItem miCloseAll;
	private JMenuItem miCloseOthers;

	/**
	 * The tabbed pane where the popup menu was last showed.
	 */
	private JTabbedPane popupTabbedPane;

	/**
	 * The tabb index where the popup menu was last showed.
	 */
	private int popupTabIndex;

	/**
	 * Custom cursors.
	 */
	private Cursor curMoveDrop, curMoveNoDrop, curLeft, curRight, curTop, curBottom;

	/**
	 * Constructs a new tabbed pane splitter.
	 * 
	 * @param tabbedPane
	 *          the original tabbed pane.
	 */
	public Splitter(JSplitableTabbedPane topPanel) {
		this.topPanel = topPanel;

		// Create the cursors.
		createCursors();

		// Create the popup menu.
		createPopupMenu();
	}

	/**
	 * Initializes the cursors.
	 */
	private void createCursors() {
		curMoveDrop = DragSource.DefaultMoveDrop;
		curMoveNoDrop = DragSource.DefaultMoveNoDrop;

		curLeft = createCustomCursor("resources/cursors/left.gif", new Point(7, 7), "leftCursor");
		curRight = createCustomCursor("resources/cursors/right.gif", new Point(7, 7), "rightCursor");
		curTop = createCustomCursor("resources/cursors/top.gif", new Point(7, 7), "topCursor");
		curBottom = createCustomCursor("resources/cursors/bottom.gif", new Point(7, 7), "bottomCursor");
	}

	/**
	 * Creates the popup menu.
	 */
	private void createPopupMenu() {
		tabbedPanePopup = new JPopupMenu();

		miClose = tabbedPanePopup.add(Resources.getString(Resources.BUTTON_CLOSE));
		miClose.addActionListener(this);

		miCloseOthers = tabbedPanePopup.add(Resources.getString(Resources.BUTTON_CLOSE_OTHERS));
		miCloseOthers.addActionListener(this);

		miCloseAll = tabbedPanePopup.add(Resources.getString(Resources.BUTTON_CLOSE_ALL));
		miCloseAll.addActionListener(this);
	}

	/**
	 * Creates a custom cursor using an image.
	 * 
	 * @param filename
	 *          the image to display when the cursor is actived
	 * @param hotSpot
	 *          the X and Y of the large cursor's hot spot.
	 * @param name
	 *          a localized description of the cursor, for Java Accessibility use.
	 * 
	 * @return the newly created cursor.
	 */
	private Cursor createCustomCursor(String filename, Point hotSpot, String name) {
		Cursor cursor = null;
		try {
			cursor = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(getClass().getResource(filename)).getImage(), hotSpot, name);
		} catch (Exception e) {
		}

		return cursor;
	}

	/**
	 * Attempt to show the popup menu.
	 * 
	 * @param e
	 *          the mouse event.
	 */
	private void attemptShowPopupMenu(MouseEvent e) {
		if (topPanel.isShowTabPopupMenu() && e.isPopupTrigger()) {
			popupTabbedPane = (JTabbedPane) e.getComponent();
			popupTabIndex = ((JTabbedPane) e.getComponent()).indexAtLocation(e.getX(), e.getY());
			if (popupTabIndex != -1) {
				// Don't forget to disable the "close others" menu item if there is only one tab.
				miCloseOthers.setEnabled(popupTabbedPane.getTabCount() > 1);
				tabbedPanePopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	/**
	 * Returns the popup menu used to display the options to close tabs.
	 * 
	 * @return the popup menu used to display the options to close tabs.
	 */
	JPopupMenu getPopupMenu() {
		return tabbedPanePopup;
	}

	/*
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// Set the origin tabbed pane. The source component is always a tabbed pane, since they are the only components which have this mouse
		// listener installed.
		tpSource = (JTabbedPane) e.getComponent();

		// Set the source index. If sourceIndex is -1, user has NOT clicked inside a tab.
		sourceIndex = tpSource.indexAtLocation(e.getX(), e.getY());

		// Attempt to show the popup menu.
		attemptShowPopupMenu(e);
	}

	/*
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// No source index, so exit.
		if (sourceIndex == -1) {
			return;
		}

		// Reset to default cursor.
		topPanel.setCursor(Cursor.getDefaultCursor());

		// First of all, set the destination tabbed pane. This is needed since a drag may not have been performed.
		setDestinationTabbedPane(e);

		// If we have to perform no split, try to move the selected tab. Otherwise, create a new tabbed pane with the selected tab from the
		// source tabbed pane.
		boolean moved;
		if (newPosition == SplitterConstants.NO_SPLIT) {
			moved = topPanel.moveTab(tpSource, tpDestination, sourceIndex, destinationIndex);
		} else {
			moved = topPanel.splitTab(tpSource, tpDestination, sourceIndex, newPosition);
		}

		// Reset fields.
		tpSource = null;
		tpDestination = null;

		sourceIndex = -1;
		destinationIndex = -1;

		rectangle = null;

		newPosition = SplitterConstants.NO_SPLIT;

		// Repaint and try to show the popup menu if a move has not been performed.
		if (!moved) {
			topPanel.repaint();
			attemptShowPopupMenu(e);
		}
	}

	/*
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) {
		// No source tab, so exit.
		if (sourceIndex == -1) {
			return;
		}

		if (tpSource == null) {
			return;
		}

		// First of all, set the destination tabbed pane.
		setDestinationTabbedPane(e);

		// Let's compute the rectangle to paint.
		Rectangle r = null;

		// The source is the same as the destination. TOP location is not allowed.
		if (tpSource.equals(tpDestination)) {
			if (tpSource.getTabCount() == 1) {
				newPosition = SplitterConstants.NO_SPLIT;

				r = SwingUtilities.convertRectangle(tpSource, SwingUtilities.getLocalBounds(tpSource), topPanel);

				topPanel.setCursor(curMoveNoDrop);
			} else {
				if (destinationIndex != -1) {
					newPosition = SplitterConstants.NO_SPLIT;

					r = SwingUtilities.convertRectangle(tpSource, tpSource.getBoundsAt(destinationIndex), topPanel);

					topPanel.setCursor(curMoveDrop);
				} else {
					// Compute the distance to the left side.
					int deltaLeft = e.getX();
					// Compute the distance to the right side.
					int deltaRight = tpDestination.getWidth() - e.getX();
					// Compute the distance to the top side.
					int deltaTop = e.getY();
					// Compute the distance to the bottom side.
					int deltaBottom = tpDestination.getHeight() - e.getY();

					// Compute the new rectangle.
					r = SwingUtilities.convertRectangle(tpSource, SwingUtilities.getLocalBounds(tpSource), topPanel);

					if (deltaLeft < MIN_DISTANCE) {
						newPosition = SplitterConstants.SPLIT_LEFT;

						r.width /= 2;

						topPanel.setCursor(curLeft);
					} else if (deltaRight < MIN_DISTANCE) {
						newPosition = SplitterConstants.SPLIT_RIGHT;

						r.x += (r.width / 2);
						r.width /= 2;

						topPanel.setCursor(curRight);
					} else if (deltaTop < MIN_DISTANCE) {
						newPosition = SplitterConstants.SPLIT_TOP;

						r.height /= 2;

						topPanel.setCursor(curTop);
					} else if (deltaBottom < MIN_DISTANCE) {
						newPosition = SplitterConstants.SPLIT_BOTTOM;

						r.y += (r.height / 2);
						r.height /= 2;

						topPanel.setCursor(curBottom);
					} else {
						newPosition = SplitterConstants.NO_SPLIT;

						topPanel.setCursor(curMoveNoDrop);
					}
				}
			}
		} else if (tpDestination != null) {
			if (destinationIndex != -1) {
				newPosition = SplitterConstants.NO_SPLIT;

				r = SwingUtilities.convertRectangle(tpDestination, tpDestination.getBoundsAt(destinationIndex), topPanel);

				topPanel.setCursor(curMoveDrop);
			} else {
				// Converts the point.
				Point p = SwingUtilities.convertPoint(tpSource, e.getPoint(), tpDestination);

				// Bounds of the component.
				Rectangle bounds = SwingUtilities.getLocalBounds(tpDestination);

				// Compute the distance to the left side.
				int deltaLeft = p.x;
				// Compute the distance to the right side.
				int deltaRight = bounds.width - p.x;
				// Compute the distance to the top side.
				int deltaTop = p.y;
				// Compute the distance to the bottom side.
				int deltaBottom = bounds.height - p.y;

				// Compute the new rectangle.
				r = SwingUtilities.convertRectangle(tpDestination, bounds, topPanel);

				if (deltaLeft < MIN_DISTANCE) {
					newPosition = SplitterConstants.SPLIT_LEFT;

					r.width /= 2;

					topPanel.setCursor(curLeft);
				} else if (deltaRight < MIN_DISTANCE) {
					newPosition = SplitterConstants.SPLIT_RIGHT;

					r.x += (r.width / 2);
					r.width /= 2;

					topPanel.setCursor(curRight);
				} else if (deltaTop < MIN_DISTANCE) {
					newPosition = SplitterConstants.SPLIT_TOP;

					r.height /= 2;

					topPanel.setCursor(curTop);
				} else if (deltaBottom < MIN_DISTANCE) {
					newPosition = SplitterConstants.SPLIT_BOTTOM;

					r.y += (r.height / 2);
					r.height /= 2;

					topPanel.setCursor(curBottom);
				} else {
					newPosition = SplitterConstants.NO_SPLIT;

					topPanel.setCursor(curMoveDrop);
				}
			}
		} else {
			newPosition = SplitterConstants.NO_SPLIT;

			topPanel.setCursor(DragSource.DefaultMoveNoDrop);
		}

		if ((r != null) && r.equals(rectangle)) {
			return;
		}

		// Get the parent graphics object.
		Graphics2D g = (Graphics2D) topPanel.getGraphics();
		g.setXORMode(Color.white);
		g.setStroke(new BasicStroke(2.0f));

		// Repaint the old rectangle.
		if (rectangle != null) {
			g.draw(rectangle);
		}

		if (r != null) {
			g.draw(r);
		}
		g.dispose();

		// Set the new rectangle.
		rectangle = r;
	}

	/*
	 * @see java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent)
	 */
	public void componentAdded(ContainerEvent e) {
	}

	/*
	 * @see java.awt.event.ContainerListener#componentRemoved(java.awt.event.ContainerEvent)
	 */
	public void componentRemoved(ContainerEvent e) {
		topPanel.checkTabbedPaneHasTabs((JTabbedPane) e.getComponent());
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (popupTabbedPane == null) {
			return;
		}

		if (e.getSource() == miClose) {
			popupTabbedPane.removeTabAt(popupTabIndex);
		} else if (e.getSource() == miCloseAll) {
			popupTabbedPane.removeAll();
		} else if (e.getSource() == miCloseOthers) {
			int tabCount = popupTabbedPane.getTabCount();
			if (tabCount > 1) {
				// Remove tabs BELOW the selected one. Remember that when a tab is rmeoved, the other tabs are shifted.
				for (int i = 0; i < popupTabIndex; i++) {
					popupTabbedPane.removeTabAt(0);
				}
				// Remove tabs ABOVE the selected one. Remember that when a tab is rmeoved, the other tabs are shifted. The tab we want to keep is
				// now at index 0.
				for (int i = 0; i < tabCount - popupTabIndex - 1; i++) {
					popupTabbedPane.removeTabAt(1);
				}
			}
		}

		popupTabbedPane = null;
		popupTabIndex = -1;
	}

	/**
	 * Sets the destination tabbed pane, using the coordinates of the mouse event.
	 * 
	 * @param e
	 *          the mouse event.
	 */
	private void setDestinationTabbedPane(MouseEvent e) {
		// The source component. This is always a JTabbedPane.
		Component source = e.getComponent();

		// Convert the point to parent component.
		Point p = SwingUtilities.convertPoint(source, e.getPoint(), topPanel);

		tpDestination = getTabbedPaneFor(topPanel, p);
		if (tpDestination != null) {
			// Convert the point to destination component.
			p = SwingUtilities.convertPoint(source, e.getPoint(), tpDestination);
			// Set the destination index.
			destinationIndex = tpDestination.indexAtLocation(p.x, p.y);
		}
	}

	/**
	 * Gets the tabbed pane which contains the given point.
	 * 
	 * @param c
	 *          the component.
	 * @param p
	 *          the point.
	 * 
	 * @return the tabbed pane which contains the given point.
	 */
	private JTabbedPane getTabbedPaneFor(Component c, Point p) {
		Component comp = c.getComponentAt(p);
		if (comp != null) {
			if (comp instanceof JTabbedPane) {
				return (JTabbedPane) comp;
			} else if (c.equals(comp)) {
				return null;
			} else {
				return getTabbedPaneFor(comp, SwingUtilities.convertPoint(c, p, comp));
			}
		}
		return null;
	}

}