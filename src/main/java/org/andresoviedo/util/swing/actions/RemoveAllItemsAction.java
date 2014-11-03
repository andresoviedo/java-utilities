package org.andresoviedo.util.swing.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * An action that can be bound to a <code>JList</code> or a <code>JTable</code> to remove all items in their model. Notice that this action
 * will work only if list model is an instance of <code>javax.swing.DefaultListModel</code> (clear() mehod needed) and table model is an
 * instance of <code>javax.swing.table.DefaultTableModel</code> (setRowCount() method needed).
 * 

 */
public class RemoveAllItemsAction extends AbstractAction {

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
	public RemoveAllItemsAction(JList list) {
		if (list == null) {
			throw new IllegalArgumentException("List is null.");
		}
		this.list = list;
	}

	/**
	 * Creates a new action with the associated table component. Notice that the action is not added to table's action map.
	 * 
	 * @param table
	 *          the table component.
	 * @throws IllegalArgumentException
	 *           if <code>table</code> is <code>null</code>.
	 */
	public RemoveAllItemsAction(JTable table) {
		if (table == null) {
			throw new IllegalArgumentException("Table is null.");
		}
		this.table = table;
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (list != null) {
			removeAllItemsInList();
		} else if (table != null) {
			removeAllItemsInTable();
		}
	}

	private void removeAllItemsInList() {
		if (list.getModel() instanceof DefaultListModel) {
			((DefaultListModel) list.getModel()).clear();
		}
	}

	private void removeAllItemsInTable() {
		if (table.getModel() instanceof DefaultTableModel) {
			((DefaultTableModel) table.getModel()).setRowCount(0);
		}
	}

}