/*
 * @(#)AbstractTreeTableModel.java	1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package org.andresoviedo.util.swing.jtreetable;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * @version 1.2 10/27/98 An abstract implementation of the TreeTableModel interface, handling the list of listeners.
 * @author Philip Milne
 */
public abstract class AbstractTreeTableModel implements TreeTableModel {

	protected EventListenerList listenerList = new EventListenerList();

	protected Object root;

	public AbstractTreeTableModel(Object root) {
		this.root = root;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(TreeModelListener.class, l);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return Object.class;
	}

	// This is not called in the JTree's default mode: use a naive implementation.
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		for (int i = 0; i < getChildCount(parent); i++) {
			if (getChild(parent, i).equals(child)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return root;
	}

	/**
	 * By default, make the column with the Tree in it the only editable one. Making this column editable causes the JTable to forward mouse
	 * and keyboard events in the Tree column to the underlying JTree.
	 * 
	 * @see org.andresoviedo.util.swing.jtreetable.TreeTableModel#isCellEditable(java.lang.Object, int)
	 */
	@Override
	public boolean isCellEditable(Object node, int column) {
		return getColumnClass(column) == TreeTableModel.class;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(TreeModelListener.class, l);
	}

	@Override
	public void setValueAt(Object aValue, Object node, int column) {
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type. The event instance is lazily created using
	 * the parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array.
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type. The event instance is lazily created using
	 * the parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
			}
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type. The event instance is lazily created using
	 * the parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
			}
		}
	}

	/**
	 * Notify all listeners that have registered interest for notification on this event type. The event instance is lazily created using
	 * the parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

}