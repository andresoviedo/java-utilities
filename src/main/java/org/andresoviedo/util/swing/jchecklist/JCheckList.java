package org.andresoviedo.util.swing.jchecklist;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.andresoviedo.util.swing.SwingUtils;
import org.andresoviedo.util.swing.jchecklist.CheckListModel.CheckMode;
import org.andresoviedo.util.swing.jchecklist.resources.Resources;

/**
 * A <code>JList</code> which items can be checked or unchecked using checkboxes.
 * 
 */
public class JCheckList extends JList {

	/**
	 * An action used to check/uncheck the selected items.
	 */
	private static class CheckAction extends AbstractAction {

		private JCheckList list;

		public CheckAction(JCheckList list) {
			super(Resources.getString(Resources.ACTION_TOGGLE));
			this.list = list;
			putValue(Action.ACTION_COMMAND_KEY, "toggle");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int[] indices = list.getSelectedIndices();
			if (indices.length == 0) {
				return;
			}

			CheckListModel model = (CheckListModel) list.getModel();
			if (indices.length == 1) {
				model.setChecked(indices[0], !model.isChecked(indices[0]));
			} else {
				boolean check = list.selectionAllChecked() ? false : true;
				for (int i = 0; i < indices.length; i++) {
					model.setChecked(indices[i], check);
				}
			}
		}
	}

	/**
	 * An action used to check/uncheck all items.
	 */
	private static class CheckAllAction extends AbstractAction {

		private boolean check;
		private JList list;

		public CheckAllAction(JList list, boolean check) {
			super(Resources.getString(check ? Resources.ACTION_CHECK_ALL : Resources.ACTION_UNCHECK_ALL));
			this.list = list;
			this.check = check;
			putValue(Action.ACTION_COMMAND_KEY, check ? "checkAll" : "uncheckAll");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(check ? KeyEvent.VK_S : KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CheckListModel model = (CheckListModel) list.getModel();
			if (model.getSize() > 0) {
				model.setChecked(0, model.getSize() - 1, check);
			}
		}

	}

	private class DataModelListener implements ListDataListener {

		@Override
		public void contentsChanged(ListDataEvent e) {
			checkActionsState();
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			checkActionsState();
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			checkActionsState();
		}

	}

	private static class MoveAction extends AbstractAction {

		private JList list;
		private boolean up;

		private MoveAction(JList list, boolean up) {
			super(Resources.getString(up ? Resources.ACTION_MOVE_UP : Resources.ACTION_MOVE_DOWN));
			this.list = list;
			this.up = up;
			putValue(Action.ACTION_COMMAND_KEY, up ? "moveUp" : "moveDown");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(up ? KeyEvent.VK_UP : KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK));

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int index = list.getSelectionModel().getLeadSelectionIndex();
			if (index != -1) {
				if (list.getModel() instanceof DefaultCheckListModel) {
					DefaultCheckListModel model = (DefaultCheckListModel) list.getModel();
					if (up) {
						model.itemUp(index);
					} else {
						model.itemDown(index);
					}
					list.setSelectedIndex(up ? index - 1 : index + 1);
					list.repaint();
				}
			}
		}

	}

	private Action aCheck;
	private Action aCheckAll;
	private Action aMoveDown;
	private Action aMoveUp;
	private Action aUncheckAll;

	/**
	 * The hotspot.
	 */
	private int hotspot;

	/**
	 * The model listener.
	 */
	private DataModelListener modelListener;

	/**
	 * Constructs a <code>JCheckList</code> with an empty model.
	 */
	public JCheckList() {
		this(new AbstractCheckListModel() {
			@Override
			public CheckMode getCheckMode() {
				return CheckMode.MULTIPLE;
			}

			@Override
			public Object getElementAt(int index) {
				return null;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public boolean isChecked(int index) {
				return false;
			}

			@Override
			public void setChecked(int index, boolean c) {
			}

			@Override
			public void setChecked(int fromIndex, int toIndex, boolean checked) {
			}
		});
	}

	/**
	 * Constructs a <code>JCheckList</code> that displays the elements in the specified array. This constructor just delegates to the
	 * <code>ListModel</code> constructor.
	 * 
	 * @param state
	 *            state of the checkboxes
	 * @param listData
	 *            the array of Objects to be loaded into the data model
	 */
	public JCheckList(boolean[] state, Object[] listData) {
		this(new DefaultCheckListModel(state, listData));
	}

	/**
	 * Constructs a <code>JCheckList</code> that displays the elements in the specified array. This constructor just delegates to the
	 * <code>ListModel</code> constructor.
	 * 
	 * @param state
	 *            state of the checkboxes
	 * @param listData
	 *            the array of Objects to be loaded into the data model
	 * @param checkMode
	 *            the check mode.
	 */
	public JCheckList(boolean[] state, Object[] listData, CheckMode checkMode) {
		this(new DefaultCheckListModel(state, listData, checkMode));
	}

	/**
	 * Constructs a <code>JCheckList</code> with the specified model.
	 * 
	 * @param dataModel
	 *            the data model.
	 */
	public JCheckList(CheckListModel dataModel) {
		this(dataModel, false);
	}

	/**
	 * Constructs a <code>CheckList</code> that displays the elements in the specified, non-<code>null</code> model. All
	 * <code>CheckList</code> constructors delegate to this one.
	 * 
	 * @param dataModel
	 *            the data model for this list
	 * @param itemsMovingAllowed
	 *            <code>true</code> to indicate that items can be moved up and down, <code>false</code> otherwise.
	 * @exception IllegalArgumentException
	 *                if <code>dataModel</code> is <code>null</code>
	 */
	public JCheckList(CheckListModel dataModel, boolean itemsMovingAllowed) {
		super(dataModel);
		setCellRenderer(new DefaultCheckListCellRenderer());
		hotspot = (new JCheckBox()).getPreferredSize().width;

		// Create the actions and bind them to the list.
		aCheck = new CheckAction(this);
		aCheckAll = new CheckAllAction(this, true);
		aUncheckAll = new CheckAllAction(this, false);
		if (itemsMovingAllowed) {
			aMoveUp = new MoveAction(this, true);
			aMoveDown = new MoveAction(this, false);
		}
		checkActionsState();

		SwingUtils.bindAction(this, aCheck);
		SwingUtils.bindAction(this, aCheckAll);
		SwingUtils.bindAction(this, aUncheckAll);
		if (itemsMovingAllowed) {
			SwingUtils.bindAction(this, aMoveUp);
			SwingUtils.bindAction(this, aMoveDown);
		}

		dataModel.addListDataListener(modelListener = new DataModelListener());

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!SwingUtilities.isLeftMouseButton(e)) {
					return;
				}

				// Don't allow modifications if the list is disabled.
				if (!JCheckList.this.isEnabled()) {
					return;
				}

				int index = locationToIndex(e.getPoint());
				if (index < 0) {
					return;
				}

				Rectangle cellBounds = getCellBounds(index, index);
				if (!cellBounds.contains(e.getPoint()) || (e.getX() > hotspot)) {
					return;
				}

				CheckListModel model = (CheckListModel) getModel();
				model.setChecked(index, !model.isChecked(index));

				e.consume();
				repaint(cellBounds);
			}
		});
		addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				checkActionsState();
			}
		});
	}

	/**
	 * Creates a popup menu to be added to the list or to the scroll pane that may hold it.
	 * 
	 * @return a popup menu.
	 */
	public JPopupMenu createPopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		popup.add(aCheck);
		popup.addSeparator();
		popup.add(aCheckAll);
		popup.add(aUncheckAll);
		if (aMoveUp != null) {
			popup.addSeparator();
			popup.add(aMoveUp);
			popup.add(aMoveDown);
		}
		return popup;
	}

	/**
	 * Returns <code>true</code> if all items are checked. Returns <code>false</code> if list's model is empty.
	 * 
	 * @return <code>true</code> if all items are checked, <code>false</code> otherwise.
	 */
	public boolean elementsAllChecked() {
		if (getModel().getSize() == 0) {
			return false;
		}
		CheckListModel model = (CheckListModel) getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (!model.isChecked(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if all items are unchecked. Returns <code>false</code> if list's model is empty.
	 * 
	 * @return <code>true</code> if all items are unchecked, <code>false</code> otherwise.
	 */
	public boolean elementsAllUnchecked() {
		if (getModel().getSize() == 0) {
			return false;
		}
		CheckListModel model = (CheckListModel) getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (model.isChecked(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the action used to check or uncheck the selected item.
	 * 
	 * @return the action used to check or uncheck the selected item.
	 */
	public Action getACheck() {
		return aCheck;
	}

	/**
	 * Returns the action used to check all items.
	 * 
	 * @return the action used to check all items.
	 */
	public Action getACheckAll() {
		return aCheckAll;
	}

	/**
	 * Returns the action used to move an item down. Will return <code>null</code> if item moving is not allowed.
	 * 
	 * @return the action used to move an item down.
	 */
	public Action getAMoveDown() {
		return aMoveDown;
	}

	/**
	 * Returns the action used to move an item up. Will return <code>null</code> if item moving is not allowed.
	 * 
	 * @return the action used to move an item up.
	 */
	public Action getAMoveUp() {
		return aMoveUp;
	}

	/**
	 * Returns the action used to uncheck all items.
	 * 
	 * @return the action used to uncheck all items.
	 */
	public Action getAUncheckAll() {
		return aUncheckAll;
	}

	/**
	 * Returns <code>true</code> if all selected items are checked. Returns <code>false</code> if no items are selected.
	 * 
	 * @return <code>true</code> if all selected items are checked, <code>false</code> otherwise.
	 */
	public boolean selectionAllChecked() {
		int[] indices = getSelectedIndices();
		if (indices.length == 0) {
			return false;
		}
		CheckListModel model = (CheckListModel) getModel();
		for (int i = 0; i < indices.length; i++) {
			if (!model.isChecked(indices[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if all selected items are unchecked. Returns <code>false</code> if no items are selected.
	 * 
	 * @return <code>true</code> if all selected items are unchecked, <code>false</code> otherwise.
	 */
	public boolean selectionAllUnchecked() {
		int[] indices = getSelectedIndices();
		if (indices.length == 0) {
			return false;
		}
		CheckListModel model = (CheckListModel) getModel();
		for (int i = 0; i < indices.length; i++) {
			if (model.isChecked(indices[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets a new model.
	 * 
	 * @param model
	 *            the model (<code>null</code> not permitted).
	 */
	public void setModel(CheckListModel model) {
		if (getModel() != null) {
			getModel().removeListDataListener(modelListener);
		}
		super.setModel(model);
		model.addListDataListener(modelListener);
		getSelectionModel().setSelectionMode(
				model.getCheckMode() == CheckMode.SINGLE ? ListSelectionModel.SINGLE_SELECTION
						: ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		checkActionsState();
	}

	private void checkActionsState() {
		int leadIndex = getSelectionModel().getLeadSelectionIndex();
		boolean indexSelected = (getSelectedIndex() >= 0);
		boolean modelEmpty = (getModel().getSize() == 0);
		boolean multipleSelection = ((CheckListModel) getModel()).getCheckMode() == CheckMode.MULTIPLE;
		aCheck.setEnabled(indexSelected);
		aCheckAll.setEnabled(multipleSelection && !modelEmpty && !elementsAllChecked());
		aUncheckAll.setEnabled(multipleSelection && !modelEmpty && !elementsAllUnchecked());
		if (aMoveUp != null) {
			aMoveUp.setEnabled((leadIndex != -1) && (leadIndex > 0));
			aMoveDown.setEnabled((leadIndex != -1) && (leadIndex < getModel().getSize() - 1));
		}
	}

}