package org.andresoviedo.util.swing.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.Icon;

import org.andresoviedo.util.swing.SwingUtils;


/**
 * An icon that draws a single character on top of another icon. The letter is always painted using white color.
 * 

 */
public class CharIcon implements Icon {

	/**
	 * The base icon (may be <code>null</code>.
	 */
	private Icon baseIcon;

	/**
	 * The character to draw.
	 */
	private char character;

	/**
	 * The color used to draw the character.
	 */
	private Color foreground;

	/**
	 * The font used to draw the character.
	 */
	private Font font;

	/**
	 * Creates a new char icon. The default foreground is white.
	 * 
	 * @param baseIcon
	 *          the base icon.
	 * @param character
	 *          the character to draw.
	 */
	public CharIcon(Icon baseIcon, char character) {
		this(baseIcon, character, Color.white);
	}

	/**
	 * Creates a new char icon.
	 * 
	 * @param baseIcon
	 *          the base icon.
	 * @param character
	 *          the character to draw.
	 * @param foreground
	 *          the color used to draw the character.
	 */
	public CharIcon(Icon baseIcon, char character, Color foreground) {
		this(baseIcon, character, foreground, null);
	}

	/**
	 * Creates a new char icon.
	 * 
	 * @param baseIcon
	 *          the base icon.
	 * @param character
	 *          the character to draw.
	 * @param foreground
	 *          the color used to draw the character.
	 * @param font
	 *          the font used to draw the character.
	 */
	public CharIcon(Icon baseIcon, char character, Color foreground, Font font) {
		this.baseIcon = baseIcon;
		this.character = character;
		this.foreground = foreground;
		this.font = font;
	}

	/**
	 * Returns the base icon.
	 * 
	 * @return the base icon.
	 */
	public Icon getBaseIcon() {
		return baseIcon;
	}

	/**
	 * Sets the base icon.
	 * 
	 * @param baseIcon
	 *          the base icon.
	 */
	public void setBaseIcon(Icon baseIcon) {
		this.baseIcon = baseIcon;
	}

	/**
	 * Returns the character that will be drawn on top of the base icon.
	 * 
	 * @return the character that will be drawn on top of the base icon.
	 */
	public char getCharacter() {
		return character;
	}

	/**
	 * Sets the character that will be drawn on top of the base icon.
	 * 
	 * @param character
	 *          the new character.
	 */
	public void setCharacter(char character) {
		this.character = character;
	}

	/**
	 * Returns the font used to draw the character (may be <code>null</code>).
	 * 
	 * @return the font used to draw the character.
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the font used to draw the character (<code>null</code> permitted).
	 * 
	 * @param font
	 *          a font object.
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Returns the color used to draw the character.
	 * 
	 * @return the color used to draw the character.
	 */
	public Color getForeground() {
		return foreground;
	}

	/**
	 * Sets the color used to draw the character.
	 * 
	 * @param foreground
	 *          an arbitrary color.
	 */
	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	/*
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return (baseIcon == null) ? 0 : baseIcon.getIconHeight();
	}

	/*
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return (baseIcon == null) ? 0 : baseIcon.getIconWidth();
	}

	/*
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (baseIcon == null) {
			return;
		}
		// Paint the base icon.
		baseIcon.paintIcon(c, g, x, y);

		// Paint the character.
		g.setColor(foreground);
		if (font != null) {
			g.setFont(font);
		}

		FontMetrics fm = SwingUtils.getFontMetrics(g.getFont(), g);
		int w = fm.charWidth(character);
		int h = fm.getAscent() + fm.getDescent();
		int x1 = x + (getIconWidth() - w) / 2;
		int y1 = y + (getIconHeight() - h) / 2 + fm.getAscent();

		g.drawString(String.valueOf(character), x1, y1);
	}

}