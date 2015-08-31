package org.andresoviedo.util.swing.icons;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * An empty icon.
 * 
 */
public class EmptyIcon implements Icon {

	/**
	 * The icon width.
	 */
	private int width;

	/**
	 * The icon height.
	 */
	private int height;

	/**
	 * Creates a new empty icon.
	 * 
	 * @param width
	 *            the icon's width.
	 * @param height
	 *            the icon's height.
	 * @throws IllegalArgumentException
	 *             if <code>width</code> or <code>height</code> are less than zero.
	 */
	public EmptyIcon(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	/**
	 * Returns the icon's height.
	 * 
	 * @return the icon's height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the icon's height.
	 * 
	 * @param height
	 *            the new height.
	 * @throws IllegalArgumentException
	 *             if <code>height</code> is less than zero.
	 */
	public void setHeight(int height) {
		if (height < 0) {
			throw new IllegalArgumentException("Height is less than 0.");
		}
		this.height = height;
	}

	/**
	 * Returns the icon's width.
	 * 
	 * @return the icon's width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the icon's width.
	 * 
	 * @param width
	 *            the new width.
	 * @throws IllegalArgumentException
	 *             if <code>width</code> is less than zero.
	 */
	public void setWidth(int width) {
		if (width < 0) {
			throw new IllegalArgumentException("Width is less than 0.");
		}
		this.width = width;
	}

	/*
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return this.width;
	}

	/*
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return this.height;
	}

	/*
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
	}

}