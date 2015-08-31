package org.andresoviedo.util.swing.jtable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public class DateCellEditor extends DefaultCellEditor {

	private JFormattedTextField ftf;

	public DateCellEditor(DateFormat format) {
		super(new JFormattedTextField(format));

		setClickCountToStart(1);

		ftf = (JFormattedTextField) getComponent();

		// React when the user presses Enter while the editor is active. (Tab is handled as specified by JFormattedTextField's
		// focusLostBehavior
		// property.)
		ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
		ftf.getActionMap().put("check", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!ftf.isEditValid()) { // The text is invalid.
					ftf.setValue(ftf.getValue());
				} else
					try { // The text is valid,
						ftf.commitEdit(); // so use it.
						ftf.postActionEvent(); // stop editing
					} catch (ParseException ex) {
					}
			}
		});
	}

	/*
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		ftf.setValue(value);
		return ftf;
	}

	/*
	 * Override to ensure that the value remains a Date.
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return (Date) ftf.getValue();
	}

	/*
	 * Override to check whether the edit is valid, setting the value if it is and complaining if it isn't. If it's OK for the editor to go
	 * away, we need to invoke the superclass's version of this method so that everything gets cleaned up.
	 * 
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		if (ftf.isEditValid()) {
			try {
				ftf.commitEdit();
			} catch (ParseException e) {
			}
		} else { // text is invalid
			ftf.setValue(ftf.getValue());
		}
		return super.stopCellEditing();
	}

}