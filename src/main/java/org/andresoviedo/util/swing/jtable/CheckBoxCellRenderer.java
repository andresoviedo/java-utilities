package org.andresoviedo.util.swing.jtable;

import java.awt.Component;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * A class to be used a checkbox cell renderer in a <code>JTable</code>.
 * 

 */
public class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

	/**
	 * Texts to show.
	 */
	private List<String> textElements = null;

	/**
	 * Creates a new <code>CheckBoxRenderer</code>.
	 */
	public CheckBoxCellRenderer() {
		this(null, UIManager.getIcon("CheckBox.icon"));
	}

	/**
	 * Creates a new <code>CheckBoxRenderer</code> with the specified text elements.
	 * 
	 * @param textElements
	 *          the text elements.
	 */
	public CheckBoxCellRenderer(List<String> textElements) {
		this(null, UIManager.getIcon("CheckBox.icon"));
		setTextElements(textElements);
	}

	/**
	 * Creates a new <code>CheckBoxRenderer</code> with the specified icons.
	 * 
	 * @param defaultIcon
	 *          the default icon.
	 * @param selectedIcon
	 *          the selected icon.
	 */
	public CheckBoxCellRenderer(Icon defaultIcon, Icon selectedIcon) {
		setIcon(defaultIcon);
		setSelectedIcon(selectedIcon);
	}

	/**
	 * Set the texts to show. All elements must be instances of <code>String</code>. The <code>null</code> item is allowed.
	 * 
	 * @param textElements
	 *          the texts to show.
	 */
	public void setTextElements(List<String> textElements) {
		this.textElements = textElements;
	}

	/*
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int,
	 * int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null) {
			setSelected(false);
		} else {
			setSelected(((Boolean) value).booleanValue());
		}

		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}

		if ((textElements != null) && (row >= 0) && (row < textElements.size())) {
			setText(textElements.get(row));
		}

		return this;
	}

}