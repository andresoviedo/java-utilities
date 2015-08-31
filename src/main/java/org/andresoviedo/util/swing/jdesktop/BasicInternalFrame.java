package org.andresoviedo.util.swing.jdesktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

/**
 * An enhanced internal frame.
 * 
 */
public class BasicInternalFrame extends JInternalFrame {

	/**
	 * Preferred toolbar button size.
	 */
	private static final Dimension BUTTON_SIZE = new Dimension(28, 28);

	/**
	 * A button that shows this frame when the user clicks it.
	 */
	protected JButton button;

	/**
	 * A button that shows or hides this frame when the user clicks it.
	 */
	protected JToggleButton toggleButton;

	/**
	 * A button used to restore and select the frame when the user clicks it. This button is added to desktop's button panel if the
	 * corresponding flag is set.
	 */
	protected JToggleButton desktopToggleButton;

	/**
	 * A menu item that shows this frame when the user clicks it.
	 */
	protected JMenuItem menuItem;

	/**
	 * A menu item that shows or hides this frame when the user clicks it.
	 */
	protected JCheckBoxMenuItem checkboxMenuItem;

	/**
	 * The action associated to the internal frame.
	 */
	private Action action;

	/**
	 * The action to be used in desktop's toolbar.
	 */
	private Action desktopAction;

	/**
	 * A flag indicating if a button has to be added to desktop's pane button panel.
	 */
	private boolean useDesktopToggleButton = true;

	/**
	 * A flag indicating that the frame has always to be centered when shown.
	 */
	private boolean alwaysCentered;

	/**
	 * Creates a new internal frame with the specified title and icon. The frame is closable, maximizable, iconifiable and resizable.
	 * 
	 * @param title
	 *            the title.
	 * @param icon
	 *            the icon.
	 */
	public BasicInternalFrame(String title, Icon icon) {
		super(title, true, true, true, true);

		if (icon != null) {
			setFrameIcon(icon);
		}
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	/**
	 * Creates a new internal frame with the specified title and icon. The frame is closable, maximizable, iconifiable and resizable. The
	 * specified component is added in the frame's content pane.
	 * 
	 * @param title
	 *            the title.
	 * @param icon
	 *            the icon.
	 * @param component
	 *            the component to be added to frame's content pane.
	 */
	public BasicInternalFrame(String title, Icon icon, JComponent component) {
		this(title, icon);

		if (component != null) {
			Container c = getContentPane();
			c.setLayout(new BorderLayout());
			c.add(component, BorderLayout.CENTER);
			pack();
		}
	}

	/**
	 * Overwritten to have a custom behaviour when this method is invoked. The view position has to be set to the top-left position of the
	 * frame.
	 * 
	 * @see javax.swing.JComponent#scrollRectToVisible(java.awt.Rectangle)
	 */
	public void scrollRectToVisible(Rectangle aRect) {
		if (getDesktopPane() instanceof BasicDesktopPane) {
			BasicDesktopPane desktop = (BasicDesktopPane) getDesktopPane();
			if (desktop.getScrollPane() != null) {
				JScrollPane sp = desktop.getScrollPane();
				sp.setVisible(false);
				sp.getViewport().setViewPosition(new Point(getX(), getY()));
				sp.setVisible(true);
			}
		} else {
			super.scrollRectToVisible(aRect);
		}
	}

	/**
	 * Returns whether the desktop toggle button has to be added to desktop's pane button panel or not.
	 * 
	 * @return <code>true</code> if the button has to be added, <code>false</code> otherwise.
	 */
	public boolean getUseDesktopToggleButton() {
		return this.useDesktopToggleButton;
	}

	/**
	 * Sets whether the desktop toggle button has to be added to desktop's pane button panel or not.
	 * 
	 * @param useDesktopToggleButton
	 *            <code>true</code> if the button has to be added, <code>false</code> otherwise.
	 */
	public void setUseDesktopToggleButton(boolean useDesktopToggleButton) {
		this.useDesktopToggleButton = useDesktopToggleButton;
	}

	/**
	 * Sets whether the internal frame has to be shown as a palette (toolbox) frame or not.
	 * 
	 * @param flag
	 *            <code>true</code> the internal frame has to be shown as a palette frame, <code>false</code> otherwise.
	 */
	public void setPalette(boolean flag) {
		putClientProperty("JInternalFrame.isPalette", Boolean.valueOf(flag));
	}

	/**
	 * Sets whether the internal frame can be dragged in the desktop pane.
	 * 
	 * @param flag
	 *            <code>true</code> if we want the internal frame to be draggable, <code>false</code> otherwise.
	 */
	public void setDraggable(boolean flag) {
		putClientProperty("JInternalFrame.isDraggable", Boolean.valueOf(flag));
	}

	/**
	 * Sets whether the internal frame has to be always centered when shown.
	 * 
	 * @param alwaysCentered
	 *            <code>true</code> if we want the internal frame to be always centered, <code>false</code> otherwise.
	 */
	public void setAlwaysCentered(boolean alwaysCentered) {
		this.alwaysCentered = alwaysCentered;
	}

	/**
	 * Returns a button that makes this frame visible when the user clicks it. This is an icon-only button by default, but its properties
	 * can be set as desired. This button can be added, for instance, to a toolbar to let the user bring up this frame.
	 * 
	 * @return a button that makes this frame visible when the user clicks it.
	 */
	public JButton getButton() {
		if (button == null) {
			createButton();
		}
		return button;
	}

	/**
	 * Creates a button that makes this frame visible when the user clicks it.
	 * 
	 * @return a button that makes this frame visible when the user clicks it.
	 */
	protected JButton createButton() {
		button = new JButton(getAction());
		button.setText(null);
		button.setPreferredSize(BUTTON_SIZE);
		button.setMaximumSize(BUTTON_SIZE);
		// Always hide the action text.
		// TODO: this is not working.
		button.putClientProperty("hideActionText", Boolean.TRUE);

		return button;
	}

	/**
	 * Returns a toggle button that shows or hides this frame when the user clicks it, depending on its selected state. This is an icon-only
	 * button by default, but its properties can be set as desired. This button can be added, for instance, to a toolbar to let the user
	 * show or hide this frame.
	 * 
	 * @return a toggle button that shows or hides this frame when the user clicks it, depending on its selected state.
	 */
	public JToggleButton getToggleButton() {
		if (toggleButton == null) {
			createToggleButton();
		}
		return toggleButton;
	}

	/**
	 * Creates a toggle button that shows or hides this frame when the user clicks it, depending on its selected state.
	 * 
	 * @return a toggle button that shows or hides this frame when the user clicks it, depending on its selected state.
	 */
	protected JToggleButton createToggleButton() {
		toggleButton = new JToggleButton(getAction());
		toggleButton.setText(null);
		toggleButton.setPreferredSize(BUTTON_SIZE);
		toggleButton.setMaximumSize(BUTTON_SIZE);
		toggleButton.setSelected(isVisible());

		return toggleButton;
	}

	/**
	 * Returns a toggle button used to restore and select the frame when the user clicks it. This button is added to desktop's button panel
	 * if the corresponding flag is set. This button is configured to display frame's icon and title.
	 * 
	 * @return a toggle button used to show and select the frame when the user clicks it.
	 */
	protected JToggleButton getDesktopToggleButton() {
		if (desktopToggleButton == null) {
			createDesktopToggleButton();
		}
		return desktopToggleButton;
	}

	/**
	 * Creates a toggle button used to restore and select the frame when the user clicks it.
	 * 
	 * @return a toggle button used to restore and select the frame when the user clicks it.
	 */
	protected JToggleButton createDesktopToggleButton() {
		if (desktopToggleButton == null) {
			desktopToggleButton = new JToggleButton(getDesktopAction());
			// desktopToggleButton.setText(getTitle());
			// desktopToggleButton.setToolTipText(getTitle());
			// desktopToggleButton.setIcon(getScaledIcon(getFrameIcon(), 16, 16));
		}
		return desktopToggleButton;
	}

	/**
	 * Returns a menu item that shows this frame when the user clicks it. This menu item is configured as a text-only menu item by default,
	 * but its properties can be set as desired.
	 * 
	 * @return a menu item that shows this frame when the user clicks it.
	 */
	public JMenuItem getMenuItem() {
		if (menuItem == null) {
			createMenuItem();
		}
		return menuItem;
	}

	/**
	 * Creates a menu item that shows this frame when the user clicks it.
	 * 
	 * @return a menu item that shows this frame when the user clicks it.
	 */
	protected JMenuItem createMenuItem() {
		menuItem = new JMenuItem(getAction());
		menuItem.setIcon(null);

		return menuItem;
	}

	/**
	 * Returns a menu item that shows or hides this frame when the user clicks it. This menu item is configured as a text-only menu item by
	 * default, but its properties can be set as desired.
	 * 
	 * @return a menu item that shows or hides this frame when the user clicks it.
	 */
	public JCheckBoxMenuItem getCheckboxMenuItem() {
		if (checkboxMenuItem == null) {
			createCheckboxMenuItem();
		}
		return checkboxMenuItem;
	}

	/**
	 * Creates a menu item that shows or hides this frame when the user clicks it.
	 * 
	 * @return a menu item that shows or hides this frame when the user clicks it.
	 */
	protected JCheckBoxMenuItem createCheckboxMenuItem() {
		checkboxMenuItem = new JCheckBoxMenuItem(getAction());
		checkboxMenuItem.setIcon(null);

		return checkboxMenuItem;
	}

	/**
	 * Returns the action bound to components associated to this frame (button, toggle button, menu item and checkbox menu item). This
	 * action shows or hides the frame depending on the component triggering it (a button always shows the frame, a toggle button may show
	 * or hide the frame depending on its selected state). This action can be bound to other components.
	 * 
	 * @return the action bound to components associated to this frame.
	 */
	public Action getAction() {
		if (action == null) {
			action = new BasicInternalFrameAction(this);
		}
		return action;
	}

	/**
	 * Returns the action to be used in desktop's toolbar.
	 * 
	 * @return the action to be used in desktop's toolbar.
	 */
	protected Action getDesktopAction() {
		if (desktopAction == null) {
			desktopAction = new DesktopAction(this);
		}
		return desktopAction;
	}

	/**
	 * Select the components associated to the frame (the toggle button and the checkbox menu item). This method is invoked by
	 * <code>BasicDesktopListener</code> when the frame is shown or hidden.
	 * 
	 * @param select
	 *            <code>true</code> if the components have to be selected, <code>false</code> otherwise.
	 */
	protected void selectAssociatedItems(boolean select) {
		if (getToggleButton() != null) {
			getToggleButton().setSelected(select);
		}
		if (getCheckboxMenuItem() != null) {
			getCheckboxMenuItem().setSelected(select);
		}
	}

	/**
	 * Overwritten to center the frame if needed.
	 * 
	 * @see java.awt.Component#show()
	 */
	public void show() {
		centerFrame();
		super.show();
	}

	/**
	 * Overwritten to center the frame if needed.
	 * 
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean aFlag) {
		centerFrame();
		super.setVisible(aFlag);
	}

	/**
	 * <p>
	 * Returns a frame preferences object encapsulating this internal frame settings. Those settings are:
	 * </p>
	 * <ol>
	 * <li>The name of the internal frame (retrieved via the <code>getName()</code> method).</li>
	 * <li>The location.</li>
	 * <li>The size.</li>
	 * <li>The state (iconified, maximized, etc.).</li>
	 * <li>The visibility.</li>
	 * </ol>
	 * 
	 * @return a frame preferences object encapsulating this internal frame settings.
	 */
	public BasicFramePreferences getFramePreferences() {
		BasicFramePreferences prefs = new BasicFramePreferences();
		prefs.setName(getName());
		prefs.setX(getX());
		prefs.setY(getY());
		prefs.setWidth(getWidth());
		prefs.setHeight(getHeight());
		prefs.setIconified(isIcon());
		prefs.setMaximized(isMaximum());
		prefs.setVisible(isVisible());

		return prefs;
	}

	/**
	 * Restores internal frame settings based on the information contained on the specified preferences object.
	 * 
	 * @param prefs
	 *            the preferences object from which to load internal frame settings.
	 */
	public void setFramePreferences(BasicFramePreferences prefs) {
		if (prefs != null) {
			// Set the maximum property to false. if true, the frame may not be resized and located correctly.
			try {
				setMaximum(false);
			} catch (PropertyVetoException e) {
			}

			setLocation(prefs.getX(), prefs.getY());
			if (isResizable()) {
				setSize(prefs.getWidth(), prefs.getHeight());
			}

			if (isIconifiable()) {
				try {
					setIcon(prefs.isIconified());
				} catch (PropertyVetoException e) {
				}
			}

			if (isMaximizable()) {
				try {
					setMaximum(prefs.isMaximized());
				} catch (PropertyVetoException e) {
				}
			}

			setVisible(prefs.isVisible());
			// TODO: location should be checked, just in case the internal frame is not visible in the desktop.
		}
	}

	/**
	 * Centers the frame in the desktop pane if the corresponding flag is set.
	 */
	private void centerFrame() {
		if (alwaysCentered) {
			if (getDesktopPane() instanceof BasicDesktopPane)
				((BasicDesktopPane) getDesktopPane()).centerFrame(this);
		}
	}

	/**
	 * Returns a scaled version of an icon.
	 * 
	 * @param icon
	 *            the icon.
	 * @param w
	 *            the new width.
	 * @param h
	 *            the new height.
	 * @return a scaled version of the specified icon.
	 */
	private static Icon getScaledIcon(Icon icon, int w, int h) {
		if ((icon != null) && (icon instanceof ImageIcon)) {
			ImageIcon imageIcon = (ImageIcon) icon;
			return new ImageIcon(imageIcon.getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT));
		}
		return null;
	}

	/**
	 * The action bound to the desktop toggle button.
	 */
	private class DesktopAction extends BasicInternalFrameAction {

		public DesktopAction(BasicInternalFrame f) {
			super(f);
			putValue(Action.SMALL_ICON, getScaledIcon(f.getFrameIcon(), 16, 16));
		}

		/*
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			showAndSelectFrame(true);
		}

		/*
		 * @see org.andresoviedo.util.swing.jdesktop.BasicInternalFrameAction#propertyChange(java.beans.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			String property = evt.getPropertyName();
			if (JInternalFrame.FRAME_ICON_PROPERTY.equals(property)) {
				putValue(Action.SMALL_ICON, getScaledIcon(f.getFrameIcon(), 16, 16));
			} else if (JInternalFrame.TITLE_PROPERTY.equals(property)) {
				putValue(Action.NAME, f.getTitle());
				putValue(Action.SHORT_DESCRIPTION, f.getTitle());
			}
		}

	}

}
