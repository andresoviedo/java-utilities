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
 * An action that can be bound to a <code>JList</code> or a <code>JTable</code> to move the selected item up or down. Notice that this
 * action will work only if list model is an instance of <code>javax.swing.DefaultListModel</code> (add() mehods needed) and table model is
 * an instance of <code>javax.swing.table.DefaultTableModel</code> (moveRow() method needed).
 * 
 */
public class MoveItemAction extends AbstractAction {

	/**
	 * A constant indicating the up direction.
	 */
	public static final int UP = 0;

	/**
	 * A constant indicating the down direction.
	 */
	public static final int DOWN = 1;

	private JList list;

	private JTable table;

	private int direction;

	/**
	 * Creates a new action with the associated list component. Notice that the action is not added to list's action map.
	 * 
	 * @param list
	 *            the list component.
	 * @param direction
	 *            the direction (one of <code>UP</code> or <code>DOWN</code>).
	 * @throws IllegalArgumentException
	 *             if <code>list</code> is <code>null</code> or <code>direction</code> is not a valid direction.
	 */
	public MoveItemAction(JList list, int direction) {
		if (list == null) {
			throw new IllegalArgumentException("List is null.");
		}
		this.checkDirection(direction);
		this.list = list;
		this.direction = direction;
		this.initialize();
	}

	/**
	 * Creates a new action with the associated table component. Notice that the action is not added to table's action map.
	 * 
	 * @param table
	 *            the table component.
	 * @param direction
	 *            the direction (one of <code>UP</code> or <code>DOWN</code>).
	 * @throws IllegalArgumentException
	 *             if <code>table</code> is <code>null</code> or <code>direction</code> is not a valid direction.
	 */
	public MoveItemAction(JTable table, int direction) {
		if (table == null) {
			throw new IllegalArgumentException("Table is null.");
		}
		this.checkDirection(direction);
		this.table = table;
		this.direction = direction;
		this.initialize();
	}

	private void checkDirection(int direction) {
		if ((direction != UP) && (direction != DOWN)) {
			throw new IllegalArgumentException("Invalid direction, should be one of UP or DOWN.");
		}
	}

	private void initialize() {
		putValue(Action.ACTION_COMMAND_KEY, (direction == UP) ? "move-selected-item-up" : "move-selected-item-down");
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke((direction == UP) ? KeyEvent.VK_UP : KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK));
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (list != null) {
			moveItemInList();
		} else if (table != null) {
			moveItemInTable();
		}
	}

	private void moveItemInList() {
		if (list.getModel() instanceof DefaultListModel) {
			// First of all, get the model.
			DefaultListModel model = (DefaultListModel) list.getModel();

			int index = list.getLeadSelectionIndex();
			if ((direction == UP) && (index > 0)) {
				model.add(index - 1, model.remove(index));

				list.addSelectionInterval(index - 1, index - 1);
			} else if ((direction == DOWN) && (index >= 0 && index < (model.getSize() - 1))) {
				model.add(index + 1, model.remove(index));

				list.addSelectionInterval(index + 1, index + 1);
			}
		}
	}

	private void moveItemInTable() {
		if (table.getModel() instanceof DefaultTableModel) {
			DefaultTableModel model = (DefaultTableModel) table.getModel();

			int index = table.getSelectionModel().getLeadSelectionIndex();

			if ((direction == UP) && (index > 0)) {
				model.moveRow(index, index, index - 1);

				if (!table.getSelectionModel().isSelectedIndex(index - 1)) {
					table.getSelectionModel().removeSelectionInterval(index, index);
				}
				table.addRowSelectionInterval(index - 1, index - 1);
			} else if ((direction == DOWN) && (index >= 0 && index < (model.getRowCount() - 1))) {
				model.moveRow(index, index, index + 1);

				if (!table.getSelectionModel().isSelectedIndex(index + 1)) {
					table.getSelectionModel().removeSelectionInterval(index, index);
				}
				table.addRowSelectionInterval(index + 1, index + 1);
			}
		}
	}

}