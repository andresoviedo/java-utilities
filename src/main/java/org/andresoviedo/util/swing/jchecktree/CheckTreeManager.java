package org.andresoviedo.util.swing.jchecktree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * This class allows turning a <code>JTree</code> into a tree which items can be check or unchecked by means of checkboxes.
 * 
 */
public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener {

	/**
	 * The associated tree.
	 */
	private JTree tree;

	/**
	 * The hotspot.
	 */
	private int hotspot;

	/**
	 * The selection model.
	 */
	private CheckTreeSelectionModel selectionModel;

	/**
	 * Indicates whether the user can select or deselect checkboxes.
	 */
	private boolean editable;

	/**
	 * Creates a new tree manager with the associated tree. The user will be able to select or deselect checkboxes.
	 * 
	 * @param tree
	 *            the associated tree.
	 */
	public CheckTreeManager(JTree tree) {
		this(tree, true);
	}

	/**
	 * Creates a new tree manager with the associated tree.
	 * 
	 * @param tree
	 *            the associated tree.
	 * @param editable
	 *            <code>true</code> if the user can select or deselect checkboxes, <code>false</code> otherwise.
	 */
	public CheckTreeManager(JTree tree, boolean editable) {
		this.tree = tree;
		this.editable = editable;
		this.hotspot = (new JCheckBox()).getPreferredSize().width;

		selectionModel = new CheckTreeSelectionModel(tree.getModel());
		selectionModel.addTreeSelectionListener(this);

		tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel));
		tree.addMouseListener(this);
	}

	/**
	 * Indicates whether the user can select or deselect checkboxes.
	 * 
	 * @return <code>true</code> if the user can select or deselect checkboxes, <code>false</code> otherwise.
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Sets whether the user can select or deselect checkboxes.
	 * 
	 * @param editable
	 *            <code>true</code> if the user can select or deselect checkboxes, <code>false</code> otherwise.
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/*
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent me) {
		if (!editable) {
			return;
		}
		TreePath path = tree.getPathForLocation(me.getX(), me.getY());
		if (path == null) {
			return;
		}
		if (me.getX() > tree.getPathBounds(path).x + hotspot) {
			return;
		}
		boolean selected = selectionModel.isPathSelected(path, true);
		selectionModel.removeTreeSelectionListener(this);
		try {
			if (selected) {
				selectionModel.removeSelectionPath(path);
			} else {
				selectionModel.addSelectionPath(path);
			}
		} finally {
			selectionModel.addTreeSelectionListener(this);
			tree.treeDidChange();
		}
	}

	/**
	 * Returns the selection model.
	 * 
	 * @return the selection model.
	 */
	public CheckTreeSelectionModel getSelectionModel() {
		return selectionModel;
	}

	/*
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {
		tree.treeDidChange();
	}

}