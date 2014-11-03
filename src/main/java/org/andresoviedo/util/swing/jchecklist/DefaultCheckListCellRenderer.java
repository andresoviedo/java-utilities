package org.andresoviedo.util.swing.jchecklist;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * The default renderer for <code>JCheckList</code>.
 * 

 */
public class DefaultCheckListCellRenderer extends JCheckBox implements ListCellRenderer {

	private static Border noFocusBorder;

	/**
	 * Constructs a default renderer object for an item in a list.
	 */
	public DefaultCheckListCellRenderer() {
		if (noFocusBorder == null) {
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
		}
		setOpaque(true);
		setBorderPainted(true);
		setBorder(noFocusBorder);
	}

	/*
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setComponentOrientation(list.getComponentOrientation());
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

		setSelected(((CheckListModel) list.getModel()).isChecked(index));
		setValue(value);

		return this;
	}

	/**
	 * Sets the <code>String</code> object for the cell being rendered to <code>value</code>.
	 * 
	 * @param value
	 *          the value.
	 */
	public void setValue(Object value) {
		setText((value == null) ? "" : value.toString());
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void validate() {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void revalidate() {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void repaint(long tm, int x, int y, int width, int height) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void repaint(Rectangle r) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		// Strings get interned.
		if (propertyName == "text")
			super.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, char oldValue, char newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, short oldValue, short newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, int oldValue, int newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, long oldValue, long newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, float oldValue, float newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, double oldValue, double newValue) {
	}

	/**
	 * Overridden for performance reasons. See the <a href="#override">Implementation Note</a> for more information.
	 */
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	}

}