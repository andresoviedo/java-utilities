package org.andresoviedo.util.swing.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

/**
 * A rectangular icon, filled with a solid color (the background) and with an optional 1-pixel border drawn.
 * 

 */
public class RectangularIcon extends EmptyIcon {

	/**
	 * The background color.
	 */
	private Color background;

	/**
	 * The border color.
	 */
	private Color borderColor;

	/**
	 * A flag used to indicate whether the border has to be painted or not.
	 */
	private boolean borderPainted;

	/**
	 * Creates a new rectangular icon. The default background is red, an a black border will be drawn.
	 * 
	 * @param width
	 *          the icon's width.
	 * @param height
	 *          the icon's height.
	 * @throws IllegalArgumentException
	 *           if <code>width</code> or <code>height</code> are less than zero.
	 */
	public RectangularIcon(int width, int height) {
		this(width, height, Color.red);
	}

	/**
	 * Creates a new rectangular icon. A black border will be drawn.
	 * 
	 * @param width
	 *          the icon's width.
	 * @param height
	 *          the icon's height.
	 * @param background
	 *          the background color.
	 * @throws IllegalArgumentException
	 *           if <code>width</code> or <code>height</code> are less than zero.
	 */
	public RectangularIcon(int width, int height, Color background) {
		this(width, height, background, true);
	}

	/**
	 * Creates a new rectangular icon. The default border color is black.
	 * 
	 * @param width
	 *          the icon's width.
	 * @param height
	 *          the icon's height.
	 * @param background
	 *          the background color.
	 * @param borderPainted
	 *          indicates whether the border has to be painted or not.
	 * @throws IllegalArgumentException
	 *           if <code>width</code> or <code>height</code> are less than zero.
	 */
	public RectangularIcon(int width, int height, Color background, boolean borderPainted) {
		this(width, height, background, borderPainted, Color.black);
	}

	/**
	 * Creates a new rectangular icon.
	 * 
	 * @param width
	 *          the icon's width.
	 * @param height
	 *          the icon's height.
	 * @param background
	 *          the background color.
	 * @param borderPainted
	 *          indicates whether the border has to be painted or not.
	 * @param borderColor
	 *          the border color.
	 * @throws IllegalArgumentException
	 *           if <code>width</code> or <code>height</code> are less than zero.
	 */
	public RectangularIcon(int width, int height, Color background, boolean borderPainted, Color borderColor) {
		super(width, height);
		this.background = background;
		this.borderColor = borderColor;
		this.borderPainted = borderPainted;
	}

	/*
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int w = getWidth();
		int h = getHeight();

		g.setColor(background);
		g.fillRect(x, y, w, h);
		if (borderPainted) {
			g.setColor(borderColor);
			g.drawRect(x, y, w, h);
		}
	}

}