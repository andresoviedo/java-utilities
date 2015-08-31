package org.andresoviedo.util.swing.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * An arrow icon. The arrow's color, size and direction can be specified.
 * 
 */
public class ArrowIcon implements Icon, SwingConstants {

	/**
	 * The direction.
	 */
	private int direction;

	/**
	 * The size.
	 */
	private int size;

	/**
	 * The color used to draw the arrow.
	 */
	private Color background;

	/**
	 * Creates a new arrow icon, with the specified size. The default direction is <code>NORTH</code>.
	 * 
	 * @param size
	 *            the icon size, in pixels.
	 * @throws IllegalArgumentException
	 *             if <code>size</code> is less than zero.
	 */
	public ArrowIcon(int size) {
		this(size, NORTH);
	}

	/**
	 * Creates a new arrow icon, with the specified direction and size.
	 * 
	 * @param size
	 *            the icon size, in pixels.
	 * @param direction
	 *            the arrow's direction (should be one of <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>, <code>WEST</code>).
	 * @throws IllegalArgumentException
	 *             if <code>size</code> is less than zero.
	 */
	public ArrowIcon(int size, int direction) {
		this(size, direction, Color.black);
	}

	/**
	 * Creates a new arrow icon, with the specified direction and size.
	 * 
	 * @param size
	 *            the icon size, in pixels.
	 * @param direction
	 *            the arrow's direction (one of <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>, <code>WEST</code>).
	 * @param background
	 *            the color used to draw the arrow.
	 * @throws IllegalArgumentException
	 *             if <code>size</code> is less than zero.
	 */
	public ArrowIcon(int size, int direction, Color background) {
		if (size < 0) {
			throw new IllegalArgumentException("Size is less than 0.");
		}
		this.size = size;
		this.direction = direction;
		this.background = background;
	}

	/**
	 * Returns the color used to draw the arrow.
	 * 
	 * @return the color used to draw the arrow.
	 */
	public Color getBackground() {
		return background;
	}

	/**
	 * Sets the color used to draw the arrow.
	 * 
	 * @param background
	 *            the color used to draw the arrow.
	 */
	public void setBackground(Color background) {
		this.background = background;
	}

	/**
	 * Returns the direction.
	 * 
	 * @return the direction.
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Sets the direction (should be one of <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>, <code>WEST</code>).
	 * 
	 * @param direction
	 *            the direction.
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * Returns the arrow's size.
	 * 
	 * @return the arrow's size.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the arrow's size.
	 * 
	 * @param size
	 *            the new size.
	 * @throws IllegalArgumentException
	 *             if <code>size</code> is less than zero.
	 */
	public void setSize(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Size is less than 0.");
		}
		this.size = size;
	}

	/*
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return size;
	}

	/*
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return size;
	}

	/*
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int mid, j;

		int lines = (size / 2);
		int offset = (size / 4);

		g.translate(x, y);
		g.setColor(background);
		switch (direction) {
		case SOUTH:
			j = offset;
			mid = (getIconWidth() - 1) / 2;
			for (int i = lines - 1; i >= 0; i--) {
				g.drawLine(mid - i, j, mid + i, j);
				j++;
			}
			break;
		case WEST:
			mid = (getIconHeight() - 1) / 2;
			for (int i = 0; i < lines; i++) {
				g.drawLine(i + offset, mid - i, i + offset, mid + i);
			}
			break;
		case EAST:
			j = offset;
			mid = (getIconHeight() - 1) / 2;
			for (int i = lines - 1; i >= 0; i--) {
				g.drawLine(j, mid - i, j, mid + i);
				j++;
			}
			break;
		default: // case NORTH:
			mid = (getIconWidth() - 1) / 2;
			for (int i = 0; i < lines; i++) {
				g.drawLine(mid - i, i + offset, mid + i, i + offset);
			}
			break;
		}
		g.translate(-x, -y);
	}
}