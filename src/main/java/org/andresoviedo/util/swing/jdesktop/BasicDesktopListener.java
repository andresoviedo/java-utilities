package org.andresoviedo.util.swing.jdesktop;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * Listens for events fired by internal frames (internal frame events and component events).
 * 
 */
public class BasicDesktopListener extends InternalFrameAdapter implements ComponentListener {

	/**
	 * The associated desktop.
	 */
	protected BasicDesktopPane desktopPane;

	/**
	 * Constructs a new basic desktop listener.
	 * 
	 * @param desktopPane
	 *            the associated desktop.
	 */
	public BasicDesktopListener(BasicDesktopPane desktopPane) {
		this.desktopPane = desktopPane;
	}

	/*
	 * @see javax.swing.event.InternalFrameListener#internalFrameOpened(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameOpened(InternalFrameEvent e) {
		if (e.getInternalFrame() instanceof BasicInternalFrame) {
			desktopPane.addButton((BasicInternalFrame) e.getInternalFrame());
		}
	}

	/*
	 * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
		if (e.getInternalFrame() instanceof BasicInternalFrame) {
			desktopPane.removeButton((BasicInternalFrame) e.getInternalFrame());
		}
	}

	/*
	 * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameActivated(InternalFrameEvent e) {
		if (e.getInternalFrame() instanceof BasicInternalFrame) {
			BasicInternalFrame f = (BasicInternalFrame) e.getInternalFrame();
			f.getDesktopToggleButton().setSelected(true);
		}
	}

	/*
	 * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
		if (e.getInternalFrame() instanceof BasicInternalFrame) {
			BasicInternalFrame f = (BasicInternalFrame) e.getInternalFrame();
			f.getDesktopToggleButton().setSelected(false);
		}
	}

	/*
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e) {
		desktopPane.activateNextFrameInLayer(((JInternalFrame) e.getComponent()).getLayer());
		if (e.getComponent() instanceof BasicInternalFrame) {
			BasicInternalFrame f = (BasicInternalFrame) e.getComponent();
			f.selectAssociatedItems(false);
			desktopPane.removeButton(f);
		}
	}

	/*
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent e) {
		if (desktopPane.getScrollPane() != null) {
			desktopPane.getScrollPane().resizeDesktop();
		}
	}

	/*
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) {
		if (desktopPane.getScrollPane() != null) {
			desktopPane.getScrollPane().revalidate();
		}
		if (e.getComponent() instanceof BasicInternalFrame) {
			BasicInternalFrame f = (BasicInternalFrame) e.getComponent();
			f.selectAssociatedItems(true);
			desktopPane.addButton(f);
		}
	}

	/*
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {
		if (desktopPane.getScrollPane() != null) {
			desktopPane.getScrollPane().resizeDesktop();
		}
	}

}