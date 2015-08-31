package org.andresoviedo.util.swing.jtaskmanager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import org.andresoviedo.util.swing.jtaskmanager.resources.Resources;

/**
 * This class is a shortcut to the task manager dialog. It's label with a small gear icon: when the user clicks it, the task manager dialog
 * shows up.
 * 
 */
public class TaskManagerTrayIcon extends JLabel implements MouseListener {

	/**
	 * Creates a new task manager tray icon.
	 */
	public TaskManagerTrayIcon() {
		setIcon(Resources.getIcon("gears_16.png"));
		setToolTipText(Resources.getString(Resources.TASK_MANAGER_TITLE));

		addMouseListener(this);
	}

	/*
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		TaskManager.getInstance().showDialog();
	}

	/*
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	}

	/*
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
	}

	/*
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
	}

}