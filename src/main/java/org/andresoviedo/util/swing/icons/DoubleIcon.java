package org.andresoviedo.util.swing.icons;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * An icon that draws a couple of icons (one after the other), separated by a specified amount of pixels. This icon can be seen as a
 * compound icon.
 * 

 */
public class DoubleIcon implements Icon {

	/**
	 * The first icon (may be <code>null</code>).
	 */
	private Icon firstIcon;

	/**
	 * The second icon (may be <code>null</code>).
	 */
	private Icon secondIcon;

	/**
	 * The distance between both icons.
	 */
	private int gap;

	/**
	 * Creates a new double icon with a default gap of 5 pixels.
	 * 
	 * @param firstIcon
	 *          the first icon.
	 * @param secondIcon
	 *          the second icon.
	 */
	public DoubleIcon(Icon firstIcon, Icon secondIcon) {
		this(firstIcon, secondIcon, 5);
	}

	/**
	 * Creates a new double icon.
	 * 
	 * @param firstIcon
	 *          the first icon.
	 * @param secondIcon
	 *          the second icon.
	 * @param gap
	 *          the distance between both icons.
	 * @throws IllegalArgumentException
	 *           if <code>gap</code> is less than zero.
	 */
	public DoubleIcon(Icon firstIcon, Icon secondIcon, int gap) {
		if (gap < 0) {
			throw new IllegalArgumentException("Gap is less than 0.");
		}
		this.firstIcon = firstIcon;
		this.secondIcon = secondIcon;
		this.gap = gap;
	}

	/**
	 * Returns the first icon.
	 * 
	 * @return the first icon.
	 */
	public Icon getFirstIcon() {
		return firstIcon;
	}

	/**
	 * Sets the first icon (<code>null</code> permitted).
	 * 
	 * @param firstIcon
	 *          the first icon.
	 */
	public void setFirstIcon(Icon firstIcon) {
		this.firstIcon = firstIcon;
	}

	/**
	 * Gets the distance between both icons.
	 * 
	 * @return the distance between both icons.
	 */
	public int getGap() {
		return gap;
	}

	/**
	 * Sets the distance between both icons.
	 * 
	 * @param gap
	 *          the distance between both icons.
	 * @throws IllegalArgumentException
	 *           if <code>gap</code> is less than zero.
	 */
	public void setGap(int gap) {
		if (gap < 0) {
			throw new IllegalArgumentException("Gap is less than 0.");
		}
		this.gap = gap;
	}

	/**
	 * Returns the second icon.
	 * 
	 * @return the second icon.
	 */
	public Icon getSecondIcon() {
		return secondIcon;
	}

	/**
	 * Sets the second icon (<code>null</code> permitted).
	 * 
	 * @param secondIcon
	 *          the second icon.
	 */
	public void setSecondIcon(Icon secondIcon) {
		this.secondIcon = secondIcon;
	}

	/*
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		int result = 0;
		if (firstIcon != null) {
			result += firstIcon.getIconWidth();
		}
		if (secondIcon != null) {
			result += secondIcon.getIconWidth();
		}
		if ((firstIcon != null) && (secondIcon != null)) {
			result += gap;
		}
		return result;
	}

	/*
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		int result = 0;
		if (firstIcon != null) {
			result = firstIcon.getIconHeight();
		}
		if (secondIcon != null) {
			result = Math.max(result, secondIcon.getIconHeight());
		}
		return result;
	}

	/*
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int spacing = 0;
		int maxIconHeight = getIconHeight();

		if (firstIcon != null) {
			firstIcon.paintIcon(c, g, x, y + ((maxIconHeight - firstIcon.getIconHeight()) / 2));
			spacing += firstIcon.getIconHeight() + gap;
		}
		if (secondIcon != null) {
			secondIcon.paintIcon(c, g, x + spacing, y + ((maxIconHeight - secondIcon.getIconHeight()) / 2));
		}
	}

}