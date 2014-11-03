package org.andresoviedo.util.swing.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

/**
 * An action that can be bound to a <code>JList</code> or a <code>JTable</code> to remove the selected items in their model. Notice that
 * this action will work only if list model is an instance of <code>javax.swing.DefaultListModel</code> (remove() mehod needed) and table
 * model is an instance of <code>javax.swing.table.DefaultTableModel</code> (removeRow() method needed).
 * 

 */
public class RemoveSelectedItemsAction extends AbstractAction {

	private JList list;

	private JTable table;

	/**
	 * Creates a new action with the associated list component. Notice that the action is not added to list's action map.
	 * 
	 * @param list
	 *          the list component.
	 * @throws IllegalArgumentException
	 *           if <code>list</code> is <code>null</code>.
	 */
	public RemoveSelectedItemsAction(JList list) {
		if (list == null) {
			throw new IllegalArgumentException("List is null.");
		}
		this.list = list;
		this.initialize();
	}

	/**
	 * Creates a new action with the associated table component. Notice that the action is not added to table's action map.
	 * 
	 * @param table
	 *          the table component.
	 * @throws IllegalArgumentException
	 *           if <code>table</code> is <code>null</code>.
	 */
	public RemoveSelectedItemsAction(JTable table) {
		if (table == null) {
			throw new IllegalArgumentException("Table is null.");
		}
		this.table = table;
		this.initialize();
	}

	private void initialize() {
		putValue(Action.ACTION_COMMAND_KEY, "remove-selected-items");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (list != null) {
			removeItemsFromList();
		} else if (table != null) {
			removeItemsFromTable();
		}
	}

	private void removeItemsFromList() {
		// First of all, get the model.
		DefaultListModel model = (DefaultListModel) list.getModel();

		int[] indices = list.getSelectedIndices();

		int counter = indices.length;
		for (int i = 0; i < counter; i++) {
			// Remove the element.
			model.remove(indices[i]);

			// All the elements have been shifted, so update the indices.
			for (int j = i; j < counter; j++) {
				indices[j]--;
			}
		}
	}

	private void removeItemsFromTable() {
		// If the user is editing a cell, cancel the edition first. If not, an exception is thrown when invoking table.editingStopped().
		if (table.isEditing()) {
			int row = table.getEditingRow();
			int col = table.getEditingColumn();
			if (table.getCellEditor(row, col) != null) {
				table.getCellEditor(row, col).cancelCellEditing();
			}
		}

		// Proceed.
		DefaultTableModel model = (DefaultTableModel) table.getModel();

		int[] indices = table.getSelectedRows();

		int counter = indices.length;
		for (int i = 0; i < counter; i++) {
			// Remove the element.
			model.removeRow(indices[i]);

			// All the elements have been shifted, so update the indices.
			for (int j = i; j < counter; j++) {
				indices[j]--;
			}
		}
	}

}