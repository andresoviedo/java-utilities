package org.andresoviedo.util.swing.icons;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * This class represents an icon consisting of a main icon and secondary icon, which is drawn on top of the other. The relative position of
 * the secondary icon can be specified. Obviously, the secondary icon should be smaller than the main icon in order to let the latest one
 * being displayed.
 * 

 */
public class OverlappedIcon implements Icon, SwingConstants {

	/**
	 * The main icon (may be <code>null</code>).
	 */
	private Icon mainIcon;

	/**
	 * The secondary icon (may be <code>null</code>).
	 */
	private Icon secondaryIcon;

	/**
	 * The position where to paint the secondary icon in relation to the main icon.
	 */
	private int secondaryIconRelativePosition;

	/**
	 * Creates a new overlapped icon.
	 * 
	 * @param mainIcon
	 *          the main icon (<code>null</code> permitted).
	 * @param secondaryIcon
	 *          the secondary icon (<code>null</code> permitted).
	 */
	public OverlappedIcon(Icon mainIcon, Icon secondaryIcon) {
		this(mainIcon, secondaryIcon, SOUTH_WEST);
	}

	/**
	 * Creates a new overlapped icon.
	 * 
	 * @param mainIcon
	 *          the main icon (<code>null</code> permitted).
	 * @param secondaryIcon
	 *          the secondary icon (<code>null</code> permitted).
	 * @param secondaryIconRelativePosition
	 *          the position where to paint the secondary icon in relation to the main icon (should be one of <code>CENTER</code>,
	 *          <code>NORTH</code>, <code>NORTH_EAST</code>, <code>NORTH_WEST</code>, <code>SOUTH</code>, <code>SOUTH_EAST</code>,
	 *          <code>SOUTH_WEST</code>, <code>EAST</code> or <code>WEST</code>).
	 */
	public OverlappedIcon(Icon mainIcon, Icon secondaryIcon, int secondaryIconRelativePosition) {
		this.mainIcon = mainIcon;
		this.secondaryIcon = secondaryIcon;
		this.secondaryIconRelativePosition = secondaryIconRelativePosition;
	}

	/**
	 * Returns the main icon.
	 * 
	 * @return the main icon.
	 */
	public Icon getMainIcon() {
		return mainIcon;
	}

	/**
	 * Sets the main icon.
	 * 
	 * @param mainIcon
	 *          the main icon.
	 */
	public void setMainIcon(Icon mainIcon) {
		this.mainIcon = mainIcon;
	}

	/**
	 * Returns the position where to paint the secondary icon in relation to the main icon.
	 * 
	 * @return the position where to paint the secondary icon in relation to the main icon.
	 */
	public int getSecondaryIconRelativePosition() {
		return secondaryIconRelativePosition;
	}

	/**
	 * Sets the position where to paint the secondary icon in relation to the main icon.
	 * 
	 * @param position
	 *          the new position (should be one of <code>CENTER</code>, <code>NORTH</code>, <code>NORTH_EAST</code>, <code>NORTH_WEST</code>,
	 *          <code>SOUTH</code>, <code>SOUTH_EAST</code>, <code>SOUTH_WEST</code>, <code>EAST</code> or <code>WEST</code>).
	 */
	public void setSecondaryIconRelativePosition(int position) {
		this.secondaryIconRelativePosition = position;
	}

	/**
	 * Returns the secondary icon.
	 * 
	 * @return the secondary icon.
	 */
	public Icon getSecondaryIcon() {
		return secondaryIcon;
	}

	/**
	 * Sets the secondary icon. May be set to <code>null</code> to display only the main icon.
	 * 
	 * @param secondaryIcon
	 *          the secondary icon.
	 */
	public void setSecondaryIcon(Icon secondaryIcon) {
		this.secondaryIcon = secondaryIcon;
	}

	/*
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return (mainIcon != null) ? mainIcon.getIconWidth() : (secondaryIcon != null) ? secondaryIcon.getIconWidth() : 0;
	}

	/*
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return (mainIcon != null) ? mainIcon.getIconHeight() : (secondaryIcon != null) ? secondaryIcon.getIconHeight() : 0;
	}

	/*
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.translate(x, y);
		// Paint the main icon.
		if (mainIcon != null) {
			mainIcon.paintIcon(c, g, 0, 0);
		}
		// Paint the secondary icon.
		if (secondaryIcon != null) {
			if (mainIcon == null) {
				secondaryIcon.paintIcon(c, g, 0, 0);
			} else {
				int sx = 0;
				int sy = 0;
				int mw = mainIcon.getIconWidth();
				int mh = mainIcon.getIconHeight();
				int sw = secondaryIcon.getIconWidth();
				int sh = secondaryIcon.getIconHeight();

				switch (secondaryIconRelativePosition) {
				case CENTER:
					sx = (mw - sw) / 2;
					sy = (mh - sh) / 2;
					break;
				case NORTH:
					sx = (mw - sw) / 2;
					break;
				case NORTH_EAST:
					sx = mw - sw;
					break;
				case EAST:
					sx = mw - sw;
					sy = (mh - sh) / 2;
					break;
				case SOUTH_EAST:
					sx = mw - sw;
					sy = mh - sh;
					break;
				case SOUTH:
					sx = (mw - sw) / 2;
					sy = mh - sh;
					break;
				case WEST:
					sy = (mh - sh) / 2;
					break;
				case NORTH_WEST:
					break;
				default: // SOUTH_WEST:
					sy = mh - sh;
					break;
				}
				secondaryIcon.paintIcon(c, g, sx, sy);
			}
		}
		g.translate(-x, -y);
	}

}