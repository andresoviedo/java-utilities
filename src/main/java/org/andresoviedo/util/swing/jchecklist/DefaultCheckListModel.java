package org.andresoviedo.util.swing.jchecklist;

import java.util.Arrays;

/**
 * The default model for a <code>JCheckList</code>.
 * 

 */
public class DefaultCheckListModel extends AbstractCheckListModel {

	/**
	 * The check mode.
	 */
	private CheckMode checkMode;

	/**
	 * The array used to save the checked state of each item.
	 */
	private boolean states[] = new boolean[0];

	/**
	 * The array used to save each item value.
	 */
	private Object[] values = new Object[0];

	/**
	 * Creates a new empty model.
	 */
	public DefaultCheckListModel() {
		this(CheckMode.MULTIPLE);
	}

	/**
	 * Creates a new empty model with the specified check mode.
	 * 
	 * @param checkMode
	 *          the check mode.
	 */
	public DefaultCheckListModel(CheckMode checkMode) {
		this.checkMode = (checkMode == null) ? CheckMode.MULTIPLE : checkMode;
	}

	/**
	 * Creates a new model with the given state of checkboxes and the given values.
	 * 
	 * @param state
	 *          state of the checkboxes. A copy of this array will NOT be created.
	 * @param values
	 *          values. A copy of this array will NOT be created.
	 */
	public DefaultCheckListModel(boolean[] state, Object[] values) {
		this(state, values, CheckMode.MULTIPLE);
	}

	/**
	 * Creates a new model with the given state of checkboxes and the given values.
	 * 
	 * @param state
	 *          state of the checkboxes. A copy of this array will NOT be created.
	 * @param values
	 *          values. A copy of this array will NOT be created.
	 */
	public DefaultCheckListModel(boolean[] state, Object[] values, CheckMode checkMode) {
		if (state.length != values.length) {
			throw new IllegalArgumentException("state.length != values.length");
		}
		this.checkMode = (checkMode == null) ? CheckMode.MULTIPLE : checkMode;
		this.states = state;
		this.values = values;
	}

	@Override
	public CheckMode getCheckMode() {
		return checkMode;
	}

	/**
	 * Returns <code>true</code> if and only if no element is checked.
	 * 
	 * @return <code>true</code> if and only if no element is checked, <code>false</code> otherwise.
	 */
	public boolean isSelectionEmpty() {
		for (int i = 0; i < states.length; i++) {
			if (states[i]) {
				return false;
			}
		}
		return true;
	}

	/*
	 */
	public boolean isChecked(int index) {
		return states[index];
	}

	/**
	 * Moves the item at the specified index down.
	 * 
	 * @param index
	 *          the index of the item to move.
	 */
	public void itemDown(int index) {
		boolean state = states[index + 1];
		Object value = values[index + 1];
		states[index + 1] = states[index];
		states[index] = state;
		values[index + 1] = values[index];
		values[index] = value;
		fireContentsChanged(this, index, index + 1);
	}

	/**
	 * Moves the item at the specified index up.
	 * 
	 * @param index
	 *          the index of the item to move.
	 */
	public void itemUp(int index) {
		boolean state = states[index - 1];
		Object value = values[index - 1];
		states[index - 1] = states[index];
		states[index] = state;
		values[index - 1] = values[index];
		values[index] = value;
		fireContentsChanged(this, index - 1, index);
	}

	/*
	 */
	public void setChecked(int index, boolean checked) {
		if (checked) {
			if (checkMode == CheckMode.SINGLE) {
				Arrays.fill(states, false);
			}
			states[index] = checked;
			fireContentsChanged(this, 0, getSize() - 1);
		} else {
			states[index] = checked;
			fireContentsChanged(this, index, index);
		}
	}

	/**
	 * Checks or unchecks the first item with the specified value.
	 * 
	 * @param value
	 *          the value to look for.
	 * @param checked
	 *          <code>true</code> checks the item, <code>false</code> unchecks it.
	 */
	public void setChecked(Object value, boolean checked) {
		if (value == null) {
			return;
		}

		for (int i = 0; i < values.length; i++) {
			if (value.equals(values[i])) {
				setChecked(i, checked);
				break;
			}
		}
	}

	/**
	 * Checks or unchecks the specified interval of items.
	 * 
	 * @param fromIndex
	 *          the index of the first item (inclusive).
	 * @param toIndex
	 *          the index of the last item (inclusive).
	 * @param checked
	 *          <code>true</code> checks the items, <code>false</code> unchecks them.
	 */
	public void setChecked(int fromIndex, int toIndex, boolean checked) {
		if (!checked || (checkMode == CheckMode.MULTIPLE)) {
			Arrays.fill(states, fromIndex, toIndex + 1, checked);
			fireContentsChanged(this, fromIndex, toIndex);
		} else if (checkMode == CheckMode.SINGLE) {
			setChecked(toIndex, true);
		}
	}

	/*
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return values.length;
	}

	/*
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return values[index];
	}

}