package org.andresoviedo.util.swing.jtable;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 * A class to be used a checkbox cell editor in a <code>JTable</code>.
 * 

 */
public class CheckBoxCellEditor extends DefaultCellEditor {

	/**
	 * Texts to show.
	 */
	private List<String> textElements = null;

	/**
	 * Construts a new <code>CheckBoxCellEditor</code>.
	 */
	public CheckBoxCellEditor() {
		this(null);
	}

	/**
	 * Construts a new <code>CheckBoxCellEditor</code> with the specified list of elements.
	 * 
	 * @param textElements
	 *          the list of text elements.
	 */
	public CheckBoxCellEditor(List<String> textElements) {
		super(new JCheckBox());

		JCheckBox checkBox = (JCheckBox) getComponent();
		checkBox.setFocusPainted(false);

		setTextElements(textElements);
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
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JCheckBox cb = (JCheckBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);

		if (isSelected) {
			cb.setBackground(table.getSelectionBackground());
			cb.setForeground(table.getSelectionForeground());
		} else {
			cb.setBackground(table.getBackground());
			cb.setForeground(table.getForeground());
		}

		if ((textElements != null) && (row >= 0) && (row < textElements.size())) {
			cb.setText(textElements.get(row));
		}

		return cb;
	}

}