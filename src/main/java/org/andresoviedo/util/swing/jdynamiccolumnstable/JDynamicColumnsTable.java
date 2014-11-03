package org.andresoviedo.util.swing.jdynamiccolumnstable;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * <p>
 * This class provides dynamic column selection to the standard JTable.
 * </p>
 * <p>
 * Different methods are provided to select the visibility and the user access for each column.
 * </p>
 * <p>
 * By default, all columns are visible and none is dynamically selectable.
 * </p>
 * 

 */
public class JDynamicColumnsTable extends JTable implements ActionListener {

	private static final Font fontPopupSelected = UIManager.getFont("MenuItem.font").deriveFont(Font.BOLD);
	private static final Font fontPopupUnselected = UIManager.getFont("MenuItem.font").deriveFont(Font.PLAIN);

	private TableColumnModel columnModel;
	private boolean[] columnVisibleTrack; // tracks which columns are being displayed (boolean).
	private Vector<TableColumn> columns; // Vector of Table Columns
	private Vector<JCheckBoxMenuItem> colMenus; // Vector of JCheckBoxMenuItem
	private JPopupMenu popupColumn; // Popup menu with column names

	/**
	 * Creates a new <code>JDynamicColumnsTable</code> component.
	 */
	public JDynamicColumnsTable() {
		super();
		// Create the popup menu.
		popupColumn = new JPopupMenu();
		// Add listener to the text area so the popup menu can come up.
		this.getTableHeader().addMouseListener(new PopupColumnListener(popupColumn));
	}

	/**
	 * Creates a new JDynamicColumnsTable component with the specified model.
	 * 
	 * @param dm
	 *          the table model.
	 */
	public JDynamicColumnsTable(TableModel dm) {
		super(dm);
		// Create the popup menu.
		popupColumn = new JPopupMenu();
		// Add listener to the text area so the popup menu can come up.
		this.getTableHeader().addMouseListener(new PopupColumnListener(popupColumn));
	}

	/*
	 * @see javax.swing.JTable#editingStopped(javax.swing.event.ChangeEvent)
	 */
	public void editingStopped(ChangeEvent e) {
		super.editingStopped(e);
		repaint();
	}

	/*
	 * @see javax.swing.JTable#setModel(javax.swing.table.TableModel)
	 */
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		init();
	}

	private void init() {
		columnModel = new DefaultTableColumnModel();
		this.setColumnModel(columnModel);
		int totalColumns = this.getModel().getColumnCount();
		columnVisibleTrack = new boolean[totalColumns];
		columns = new Vector<TableColumn>(totalColumns);
		colMenus = new Vector<JCheckBoxMenuItem>(totalColumns);
		Hashtable<String, JCheckBoxMenuItem> menuNames = new Hashtable<String, JCheckBoxMenuItem>(); // menu names for sorting the popup
		for (int i = 0; i < totalColumns; i++) {
			String columnName = this.getModel().getColumnName(i);
			TableColumn tc = new TableColumn(i);
			tc.setHeaderValue(columnName);
			columns.add(i, tc);
			columnVisibleTrack[i] = false;
			JCheckBoxMenuItem menu = new JCheckBoxMenuItem(columnName);
			menu.setFont(fontPopupUnselected);
			menu.addActionListener(this);
			menu.setVisible(false); // Hide popup menu entry by default
			colMenus.add(i, menu);
			setVisibleColumn(i, true); // Set column visible by default
			setDynamicColumn(i, true);
			menuNames.put(columnName, menu);
		}
		// Add sorted menu entries
		Vector<String> keys = new Vector<String>(menuNames.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			popupColumn.add(menuNames.get(keys.elementAt(i)));
		}
	}

	/**
	 * Sets the visible columns.
	 * 
	 * @param col
	 *          Array with indexes of the columns to show.
	 */
	public void setVisibleColumns(int[] col) {
		int totalColumns = this.getModel().getColumnCount();
		// hide all
		for (int i = 0; i < totalColumns; i++)
			setVisibleColumn(i, false);

		// set visible only the selection
		for (int i = 0; i < col.length; i++) {
			int c = col[i];
			if (c < totalColumns) {
				setVisibleColumn(c, true);
			}
		}
	}

	/**
	 * Sets the visibility of the column at the specified index.
	 * 
	 * @param column
	 *          Column index
	 * @param visible
	 *          True to show the column. False to hide the column.
	 */
	public void setVisibleColumn(int column, boolean visible) {
		int totalColumns = this.getModel().getColumnCount();
		if (visible) {
			if (column < totalColumns && !columnVisibleTrack[column]) {
				columnModel.addColumn((TableColumn) columns.get(column));
				JCheckBoxMenuItem colMenu = (JCheckBoxMenuItem) colMenus.get(column);
				colMenu.setSelected(true);
				colMenu.setFont(fontPopupSelected);
				columnVisibleTrack[column] = true;
			}
		} else {
			if (column < totalColumns && columnVisibleTrack[column]) {
				columnModel.removeColumn((TableColumn) columns.get(column));
				JCheckBoxMenuItem colMenu = (JCheckBoxMenuItem) colMenus.get(column);
				colMenu.setSelected(false);
				colMenu.setFont(fontPopupUnselected);
				columnVisibleTrack[column] = false;
			}
		}
	}

	/**
	 * Returns an array with the indexes of the currently visible columns.
	 * 
	 * @return an array with the indexes of the currently visible columns.
	 */
	public int[] getVisibleColumns() {
		int[] ret;
		if (columnModel != null) {
			ret = new int[columnModel.getColumnCount()];
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				ret[i] = columns.indexOf(columnModel.getColumn(i));
			}
		} else {
			ret = new int[0];
		}
		return ret;
	}

	/**
	 * Toggles the visibility of the column at the specified index.
	 * 
	 * @param col
	 *          the column index.
	 * @return <code>true</code> if the status of the column has been changed, <code>false</code> if no change could be done (because the last
	 *         visible column can't be hidden).
	 */
	private boolean toggleVisibleColumn(int col) {
		if (columnVisibleTrack[col]) {
			// Ensure that at least one column remains visible.
			if (columnModel.getColumnCount() > 1 || (columnModel.getColumnCount() == 1 && !columnVisibleTrack[col])) {
				setVisibleColumn(col, false);
			} else {
				return false;
			}
		} else {
			setVisibleColumn(col, true);
		}
		return true;
	}

	/**
	 * Specifies a group of columns that could change its visibility by the user at runtime.
	 * 
	 * @param cols
	 *          the array of column indexes.
	 */
	public void setDynamicColumns(int[] cols) {
		for (int i = 0; i < cols.length; i++) {
			setDynamicColumn(cols[i], true);
		}
	}

	/**
	 * Allows or forbids a column to change its visibility at runtime by the user.
	 * 
	 * @param column
	 *          the column index.
	 * @param selectable
	 *          <code>true</code> to allow the column to be selectable by the user, <code>false</code> to avoid the user from changing its
	 *          visibility.
	 */
	public void setDynamicColumn(int column, boolean selectable) {
		int totalColumns = this.getModel().getColumnCount();
		if (column < totalColumns) {
			((JCheckBoxMenuItem) colMenus.get(column)).setVisible(selectable);
		}
	}

	/**
	 * Handles popup menu actions.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		JCheckBoxMenuItem source = (JCheckBoxMenuItem) (e.getSource());
		int col = colMenus.indexOf(source);
		if (!toggleVisibleColumn(col)) {
			source.setSelected(!source.getState());
		}
	}

	private class PopupColumnListener extends MouseAdapter {

		private JPopupMenu popup;

		public PopupColumnListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		/*
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		/*
		 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

}