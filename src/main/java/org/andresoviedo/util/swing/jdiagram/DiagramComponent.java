package org.andresoviedo.util.swing.jdiagram;

import java.awt.Color;
import java.awt.Image;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andresoviedo.util.swing.jdiagram.ui.DiagramComponentUI;

public class DiagramComponent extends JComponent implements ItemSelectable, ItemListener, ChangeListener {

	private static final String uiClassID = "DiagramComponentUI";

	/**
	 * The header text.
	 */
	private String headerText;

	/**
	 * The icon to be displayed.
	 */
	private Icon icon;

	/**
	 * The icon to be displayed when the component is disabled.
	 */
	private Icon disabledIcon;

	/**
	 * Indicates if the disabled icon has been explicitly set.
	 */
	private boolean disabledIconSet = false;

	// Colors.
	private Color headerBackground;
	private Color headerForeground;
	private Color headerSelectionBackground;
	private Color headerSelectionForeground;

	private boolean focusPainted = true;

	/**
	 * The component model.
	 */
	private DiagramComponentModel model;

	private ChangeEvent changeEvent = null;

	/**
	 * Creates a new diagram component.
	 */
	public DiagramComponent() {
		this(null, null);
	}

	/**
	 * Creates a new diagram component with the specified header text and icon.
	 * 
	 * @param headerText
	 *            the header text.
	 * @param icon
	 *            the icon.
	 */
	public DiagramComponent(String headerText, Icon icon) {
		model = new DiagramComponentModel();
		model.addChangeListener(this);
		model.addItemListener(this);

		setHeaderText(headerText);
		setIcon(icon);
		setOpaque(true);
		setFocusable(true);
		updateUI();
	}

	/**
	 * Returns the L&F object that renders this component.
	 * 
	 * @return DiagramComponentUI object.
	 */
	public DiagramComponentUI getUI() {
		return (DiagramComponentUI) ui;
	}

	/**
	 * Sets the L&F object that renders this component.
	 * 
	 * @param ui
	 *            the DiagramComponentUI L&F object
	 */
	public void setUI(DiagramComponentUI ui) {
		super.setUI(ui);
	}

	/**
	 * Resets the UI property to a value from the current look and feel.
	 */
	public void updateUI() {
		setUI((DiagramComponentUI) UIManager.getUI(this));
	}

	/*
	 * @see javax.swing.JComponent#getUIClassID()
	 */
	public String getUIClassID() {
		return uiClassID;
	}

	/**
	 * Adds a <code>ChangeListener</code> to the component.
	 * 
	 * @param l
	 *            the listener to be added.
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	/**
	 * Removes a ChangeListener from the component.
	 * 
	 * @param l
	 *            the listener to be removed.
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
	 *            the <code>ItemListener</code> to be added
	 */
	public void addItemListener(ItemListener l) {
		listenerList.add(ItemListener.class, l);
	}

	/**
	 * Removes an <code>ItemListener</code> from the component.
	 * 
	 * @param l
	 *            the listener to be removed.
	 */
	public void removeItemListener(ItemListener l) {
		listenerList.remove(ItemListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is created lazily.
	 */
	protected void fireItemStateChanged(ItemEvent event) {
		// Guaranteed to return a non-null array.
		Object[] listeners = listenerList.getListenerList();
		ItemEvent e = null;
		// Process the listeners last to first, notifying those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ItemListener.class) {
				// Lazily create the event.
				if (e == null) {
					e = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this, event.getStateChange());
				}
				((ItemListener) listeners[i + 1]).itemStateChanged(e);
			}
		}
	}

	/*
	 * @see java.awt.ItemSelectable#getSelectedObjects()
	 */
	public Object[] getSelectedObjects() {
		return null;
	}

	/**
	 * Returns the component's model.
	 * 
	 * @return the component's model.
	 */
	public DiagramComponentModel getModel() {
		return model;
	}

	/**
	 * Gets the header text.
	 * 
	 * @return the header text.
	 */
	public String getHeaderText() {
		return headerText;
	}

	/**
	 * Sets a new header text for this component.
	 * 
	 * @param headerText
	 *            the new header text.
	 */
	public void setHeaderText(String headerText) {
		String oldValue = this.headerText;
		this.headerText = headerText;

		// Fire a property change event.
		firePropertyChange("headerText", oldValue, headerText);

		// Repaint if needed.
		if ((headerText == null) || (oldValue == null) || !headerText.equals(oldValue)) {
			repaint();
		}
	}

	/**
	 * Gets the icon the component will display.
	 * 
	 * @return the icon the component will display.
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Sets a new icon to display.
	 * 
	 * @param icon
	 *            the new icon to display.
	 */
	public void setIcon(Icon icon) {
		Icon oldValue = this.icon;
		this.icon = icon;

		if ((icon != oldValue) && !disabledIconSet) {
			disabledIcon = null;
		}

		// Fire a property change event when moved to ComponentUI.
		this.repaint();
	}

	/**
	 * Gets the disabled icon for this diagram component.
	 * 
	 * @return the disabled icon for this diagram component.
	 */
	public Icon getDisabledIcon() {
		if (!disabledIconSet && (disabledIcon == null) && (icon != null) && (icon instanceof ImageIcon)) {
			Image grayImage = GrayFilter.createDisabledImage(((ImageIcon) icon).getImage());
			disabledIcon = new ImageIcon(grayImage);
		}
		return disabledIcon;
	}

	/**
	 * Sets a custom disabled icon for this diagram component.
	 * 
	 * @param disabledIcon
	 *            the disabled icon.
	 */
	public void setDisabledIcon(Icon disabledIcon) {
		this.disabledIcon = disabledIcon;
		disabledIconSet = (disabledIcon != null);
	}

	public Color getHeaderBackground() {
		return headerBackground;
	}

	public void setHeaderBackground(Color headerBackground) {
		Color oldValue = this.headerBackground;
		this.headerBackground = headerBackground;

		// Fire a property change event.
		firePropertyChange("headerBackground", oldValue, headerBackground);

		this.repaint();
	}

	public Color getHeaderForeground() {
		return headerForeground;
	}

	public void setHeaderForeground(Color headerForeground) {
		Color oldValue = this.headerForeground;
		this.headerForeground = headerForeground;

		// Fire a property change event.
		firePropertyChange("headerForeground", oldValue, headerForeground);

		this.repaint();
	}

	public Color getHeaderSelectionBackground() {
		return headerSelectionBackground;
	}

	public void setHeaderSelectionBackground(Color headerSelectionBackground) {
		Color oldValue = this.headerSelectionBackground;
		this.headerSelectionBackground = headerSelectionBackground;

		// Fire a property change event.
		firePropertyChange("headerSelectionBackground", oldValue, headerSelectionBackground);

		this.repaint();
	}

	public Color getHeaderSelectionForeground() {
		return headerSelectionForeground;
	}

	public void setHeaderSelectionForeground(Color headerSelectionForeground) {
		Color oldValue = this.headerSelectionForeground;
		this.headerSelectionForeground = headerSelectionForeground;

		firePropertyChange("headerSelectionForeground", oldValue, headerSelectionForeground);

		this.repaint();
	}

	public boolean isFocusPainted() {
		return focusPainted;
	}

	public void setFocusPainted(boolean focusPainted) {
		boolean oldValue = this.focusPainted;
		this.focusPainted = focusPainted;

		firePropertyChange("focusPainted", oldValue, focusPainted);
		if ((focusPainted != oldValue) && isFocusOwner()) {
			this.repaint();
		}
	}

	public boolean isHighlighted() {
		return model.ishighlighted();
	}

	public void setHighlighted(boolean highlighted) {
		model.setHighlighted(highlighted);
	}

	public boolean isSelected() {
		return model.isSelected();
	}

	public void setSelected(boolean selected) {
		model.setSelected(selected);
	}

	/*
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		model.setEnabled(b);
	}

	/*
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		fireStateChanged();
	}

	/*
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		fireItemStateChanged(e);
	}

}