package org.andresoviedo.util.swing.jdesktop;

/**
 * A set of frame preferences.
 * 
 */
public class BasicFramePreferences {

	/**
	 * The name of the frame.
	 */
	private String name;

	/**
	 * The top-left corner X coordinate.
	 */
	private int x;

	/**
	 * The top-left corner Y coordinate.
	 */
	private int y;

	/**
	 * Frame width.
	 */
	private int width;

	/**
	 * Frame height.
	 */
	private int height;

	/**
	 * Indicates if the frame is visible or not.
	 */
	private boolean visible;

	/**
	 * Indicates if the frame is iconified.
	 */
	private boolean iconified;

	/**
	 * Indicates if the frame is maximized.
	 */
	private boolean maximized;

	/**
	 * Returns the frame height.
	 * 
	 * @return the frame height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the frame height.
	 * 
	 * @param height
	 *            the new height.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Returns frame's name.
	 * 
	 * @return frame's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets frame's name.
	 * 
	 * @param name
	 *            the new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the frame width.
	 * 
	 * @return the frame width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the frame width.
	 * 
	 * @param width
	 *            the frame width.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns the top-left corner X coordinate.
	 * 
	 * @return the top-left corner X coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the top-left corner X coordinate.
	 * 
	 * @param x
	 *            the new X coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns the top-left corner Y coordinate.
	 * 
	 * @return the top-left corner Y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the top-left corner Y coordinate.
	 * 
	 * @param y
	 *            the new Y coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns whether the frame is visible or not.
	 * 
	 * @return <code>true</code> if the frame is visible, <code>false</code> otherwise.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets whether the frame is visible or not.
	 * 
	 * @param visible
	 *            <code>true</code> if the frame is visible, <code>false</code> otherwise.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns whether the frame is iconified or not.
	 * 
	 * @return <code>true</code> if the frame is iconified, <code>false</code> otherwise.
	 */
	public boolean isIconified() {
		return iconified;
	}

	/**
	 * Sets whether the frame is iconified or not.
	 * 
	 * @param iconified
	 *            <code>true</code> if the frame is iconified, <code>false</code> otherwise.
	 */
	public void setIconified(boolean iconified) {
		this.iconified = iconified;
	}

	/**
	 * Returns whether the frame is maximized or not.
	 * 
	 * @return <code>true</code> if the frame is maximized, <code>false</code> otherwise.
	 */
	public boolean isMaximized() {
		return maximized;
	}

	/**
	 * Sets whether the frame is maximized or not.
	 * 
	 * @param maximized
	 *            <code>true</code> if the frame is maximized, <code>false</code> otherwise.
	 */
	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

}