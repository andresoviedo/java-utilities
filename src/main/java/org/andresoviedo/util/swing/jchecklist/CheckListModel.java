package org.andresoviedo.util.swing.jchecklist;

import javax.swing.ListModel;

/**
 * A custom model for <code>JChecklist</code>.
 * 

 */
public interface CheckListModel extends ListModel {

	/**
	 * The list of available check modes.
	 */
	public static enum CheckMode {

		/**
		 * Only one item is allowed to be checked.
		 */
		SINGLE,

		/**
		 * Multiple items can be checked.
		 */
		MULTIPLE;
	}

	/**
	 * Returns the check mode.
	 * 
	 * @return the check mode.
	 */
	public CheckMode getCheckMode();

	/**
	 * Returns the state of the checkbox for the specified index.
	 * 
	 * @param index
	 *          the requested index.
	 */
	public boolean isChecked(int index);

	/**
	 * Changes the state of the checkbox for the specified index.
	 * 
	 * @param index
	 *          index of an item.
	 * @param checked
	 *          new state.
	 */
	public void setChecked(int index, boolean checked);

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
	public void setChecked(int fromIndex, int toIndex, boolean checked);

}
