package org.andresoviedo.util.swing.jdesktop;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

/**
 * An action that shows or hides the frame depending on user selection.
 * 
 */
class BasicInternalFrameAction extends AbstractAction implements PropertyChangeListener {

	/**
	 * The frame associated to the action.
	 */
	protected BasicInternalFrame f;

	/**
	 * Constructs a new action with the specified frame associated.
	 * 
	 * @param f
	 *            the associated frame.
	 */
	public BasicInternalFrameAction(BasicInternalFrame f) {
		this.f = f;
		this.f.addPropertyChangeListener(this);

		putValue(Action.NAME, f.getTitle());
		putValue(Action.ACTION_COMMAND_KEY, f.getName());
		putValue(Action.SMALL_ICON, f.getFrameIcon());
		putValue(Action.SHORT_DESCRIPTION, f.getTitle());
	}

	/**
	 * Interfície ActionListener.
	 * 
	 * Cal tenir en compte que el mètode isSelected() de la classe AbstractButton retorna sempre 'false' en el cas de tenir un JButton o un
	 * JMenuItem. En ambdós casos, doncs, caldrà fer visible la finestra.
	 */
	public void actionPerformed(ActionEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		if (b instanceof JButton || b.getClass().equals(JMenuItem.class)) {
			showAndSelectFrame(true);
		} else {
			if (b.isSelected()) {
				showAndSelectFrame(true);
			} else {
				closeFrame();
			}
		}
	}

	/**
	 * Sets the frame visible and selects it.
	 * 
	 * @param deiconify
	 *            specifies whether the frame has to be deiconified (if it's iconified) or not.
	 */
	protected void showAndSelectFrame(boolean deiconify) {
		if (f.isIcon() && deiconify) {
			try {
				f.setIcon(false);
			} catch (PropertyVetoException e) {
			}
		}

		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
		}

		f.scrollRectToVisible(f.getBounds());
	}

	/**
	 * Closes the frame.
	 */
	protected void closeFrame() {
		f.doDefaultCloseAction();

		// With some versions of JRE, if the frame is iconified the desktop icon is not hidden.
		if (f.getDesktopIcon() != null) {
			f.getDesktopIcon().setVisible(false);
		}
		// If the frame was iconified, its associated button won't be removed from desktop pane's button panel, so remove it manually.
		if (f.getDesktopPane() instanceof BasicDesktopPane) {
			((BasicDesktopPane) f.getDesktopPane()).removeButton(f);
		}
	}

	/*
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		if (JInternalFrame.FRAME_ICON_PROPERTY.equals(property)) {
			putValue(Action.SMALL_ICON, f.getFrameIcon());
		} else if (JInternalFrame.TITLE_PROPERTY.equals(property)) {
			putValue(Action.NAME, f.getTitle());
			putValue(Action.SHORT_DESCRIPTION, f.getTitle());
		}
	}

}
