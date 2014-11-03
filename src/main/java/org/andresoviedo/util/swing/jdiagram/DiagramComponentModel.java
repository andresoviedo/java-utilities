package org.andresoviedo.util.swing.jdiagram;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DiagramComponentModel implements ItemSelectable {

	public final static int HIGHLIGHTED = 1 << 0;

	public final static int SELECTED = 1 << 1;

	public final static int ENABLED = 1 << 2;

	protected int stateMask = 0;

	protected DiagramComponentGroup group = null;

	protected ChangeEvent changeEvent = null;

	protected EventListenerList listenerList = new EventListenerList();

	public DiagramComponentModel() {
		stateMask = 0;
		setEnabled(true);
	}

	public boolean ishighlighted() {
		return (stateMask & HIGHLIGHTED) != 0;
	}

	public void setHighlighted(boolean highlighted) {
		if (ishighlighted() == highlighted) {
			return;
		}

		if (highlighted) {
			stateMask |= HIGHLIGHTED;
		} else {
			stateMask &= ~HIGHLIGHTED;
		}

		fireStateChanged();
	}

	public boolean isSelected() {
		return (stateMask & SELECTED) != 0;
	}

	public void setSelected(boolean selected) {
		DiagramComponentGroup group = getGroup();
		if (group != null) {
			group.setSelected(this, selected);
			selected = group.isSelected(this);
		}

		if (isSelected() == selected) {
			return;
		}

		if (selected) {
			stateMask |= SELECTED;
		} else {
			stateMask &= ~SELECTED;
		}

		fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this, selected ? ItemEvent.SELECTED : ItemEvent.DESELECTED));

		fireStateChanged();
	}

	public boolean isEnabled() {
		return (stateMask & ENABLED) != 0;
	}

	public void setEnabled(boolean enabled) {
		if (isSelected() == enabled) {
			return;
		}

		if (enabled) {
			stateMask |= ENABLED;
		} else {
			stateMask &= ~ENABLED;
		}

		fireStateChanged();
	}

	public DiagramComponentGroup getGroup() {
		return group;
	}

	public void setGroup(DiagramComponentGroup group) {
		this.group = group;
	}

	/**
	 * Adds a <code>ChangeListener</code> to the diagram component.
	 * 
	 * @param l
	 *          the listener to add.
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	/**
	 * Removes a <code>ChangeListener</code> from the diagram component.
	 * 
	 * @param l
	 *          the listener to remove.
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is created lazily.
	 */
	protected void fireStateChanged() {
		// Guaranteed to return a non-null array.
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event.
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	/**
	 * Adds an <code>ItemListener</code> to the component.
	 * 
	 * @param l
	 *          the listener to add.
	 */
	public void addItemListener(ItemListener l) {
		listenerList.add(ItemListener.class, l);
	}

	/**
	 * Removes an <code>ItemListener</code> from the component.
	 * 
	 * @param l
	 *          the listener to remove.
	 */
	public void removeItemListener(ItemListener l) {
		listenerList.remove(ItemListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type.
	 * 
	 * @param e
	 *          the <code>ItemEvent</code> to deliver to listeners
	 * @see EventListenerList
	 */
	protected void fireItemStateChanged(ItemEvent e) {
		// Guaranteed to return a non-null array.
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ItemListener.class) {
				((ItemListener) listeners[i + 1]).itemStateChanged(e);
			}
		}
	}

	/**
	 * Overwritten to return <code>null</code>.
	 * 
	 * @see java.awt.ItemSelectable#getSelectedObjects()
	 */
	public Object[] getSelectedObjects() {
		return null;
	}

}