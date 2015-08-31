package org.andresoviedo.util.swing.jdesktop;

import java.awt.Container;
import java.beans.PropertyVetoException;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

/**
 * A custom desktop manager that keeps into account the draggable property of an internal frame. It also selects the next frame when the
 * current selected frame is iconified.
 * 
 */
public class BasicDesktopManager extends DefaultDesktopManager {

	/**
	 * Name of the property indicating if a frame can be dragged or not.
	 */
	public static final String IS_DRAGGABLE_PROPERTY = "InternalFrame.draggable";

	/**
	 * Managed desktop pane.
	 */
	private BasicDesktopPane desktop;

	/**
	 * Constructs a new basic desktop manager.
	 * 
	 * @param desktop
	 *            the associated desktop pane.
	 */
	public BasicDesktopManager(BasicDesktopPane desktop) {
		this.desktop = desktop;
	}

	/**
	 * Overwritten to activate the next frame in the layer holding the frame that has been iconified.
	 * 
	 * @see javax.swing.DesktopManager#iconifyFrame(javax.swing.JInternalFrame)
	 */
	public void iconifyFrame(JInternalFrame f) {
		super.iconifyFrame(f);
		desktop.activateNextFrameInLayer(f.getLayer());
	}

	/**
	 * Overwritten to check whether an internal frame can be dragged or not.
	 * 
	 * @see javax.swing.DesktopManager#dragFrame(javax.swing.JComponent, int, int)
	 */
	public void dragFrame(JComponent f, int newX, int newY) {
		Object o = f.getClientProperty(IS_DRAGGABLE_PROPERTY);
		if (!Boolean.FALSE.equals(o)) {
			super.dragFrame(f, newX, newY);
		}
	}

	/*
	 * @see javax.swing.DesktopManager#activateFrame(javax.swing.JInternalFrame)
	 */
	public void activateFrame(JInternalFrame f) {
		Container p = f.getParent();
		JInternalFrame currentlyActiveFrame = desktop.getSelectedFrameInLayer(f.getLayer());

		if (p == null) {
			// If the frame is not in parent, its icon maybe, check it.
			p = f.getDesktopIcon().getParent();
			if (p == null) {
				return;
			}
		}
		// We only need to keep track of the currentActive InternalFrame, if any.
		if (currentlyActiveFrame == null) {
			desktop.setSelectedFrameInLayer(f);
		} else if (currentlyActiveFrame != f) {
			// If not the same frame as the current active we deactivate the current.
			if (currentlyActiveFrame.isSelected()) {
				try {
					currentlyActiveFrame.setSelected(false);
				} catch (PropertyVetoException e2) {
				}
			}
			desktop.setSelectedFrameInLayer(f);
		}
		f.moveToFront();
	}

	/*
	 * @see javax.swing.DesktopManager#deactivateFrame(javax.swing.JInternalFrame)
	 */
	public void deactivateFrame(JInternalFrame f) {
		JInternalFrame selectedFrame = desktop.getSelectedFrameInLayer(f.getLayer());
		if (selectedFrame == f) {
			desktop.removeSelectedFrameInLayer(f.getLayer());
		}
	}

}