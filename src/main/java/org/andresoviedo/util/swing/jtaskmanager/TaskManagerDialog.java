package org.andresoviedo.util.swing.jtaskmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.andresoviedo.util.swing.jtaskmanager.resources.Resources;

/**
 * The task manager dialogs lets the user know the tasks currently being performed.
 * 
 */
public class TaskManagerDialog extends JDialog {

	private JPanel pnlTasks;

	private JPanel pnlDummy;

	private JCheckBox cbHideWhenPossible;

	private JButton btnHide;

	private Action closeAction;

	/**
	 * Creates a new task manager dialog.
	 */
	public TaskManagerDialog(Frame owner) {
		super(owner);

		// Create the close action.
		closeAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};

		try {
			installComponents();
			installListeners();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Bind a keystroke to an action to close the dialog when user presses "ESC".
		getRootPane().getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "close");
		getRootPane().getActionMap().put("close", closeAction);

		// Configure dialog properties.
		setTitle(Resources.getString(Resources.TASK_MANAGER_TITLE));
		setModal(true);
		setResizable(false);
		getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
		pack();
	}

	/**
	 * Installs GUI components.
	 */
	private void installComponents() {
		pnlTasks = new JPanel(new GridBagLayout());
		pnlDummy = new JPanel();

		JScrollPane sp = new JScrollPane(pnlTasks);
		sp.setPreferredSize(new Dimension(500, 300));

		cbHideWhenPossible = new JCheckBox(Resources.getString(Resources.TASK_MANAGER_BUTTON_HIDE_WHEN_POSSIBLE), true);

		btnHide = new JButton(closeAction);
		btnHide.setText(Resources.getString(Resources.TASK_MANAGER_BUTTON_HIDE));

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(cbHideWhenPossible);
		p.add(Box.createHorizontalGlue());
		p.add(btnHide);

		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout(5, 5));
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.add(new JLabel(Resources.getString(Resources.TASK_MANAGER_LABEL_TASKS), JLabel.LEFT), BorderLayout.NORTH);
		contentPane.add(sp, BorderLayout.CENTER);
		contentPane.add(p, BorderLayout.SOUTH);
	}

	/**
	 * Installs listeners as needed.
	 */
	private void installListeners() {
	}

	/**
	 * Loads all tasks, that is, creates a new task panel for each one.
	 */
	protected void loadTasks() {
		// First of all, remove all components.
		pnlTasks.removeAll();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 5, 5, 5);
		gbc.weightx = 1.0;

		TaskPanel taskPanel = null;
		Task[] tasks = TaskManager.getInstance().getTasks();
		for (int i = 0; i < tasks.length; i++) {
			taskPanel = new TaskPanel(tasks[i]);
			pnlTasks.add(taskPanel, gbc);
		}

		// Always add the dummy panel after the last component to push up all.
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weighty = 1.0;

		pnlTasks.add(pnlDummy, gbc);
		pnlTasks.validate();
	}

	/**
	 * Reloads all tasks.
	 */
	protected void refresh() {
		loadTasks();
	}

	/**
	 * Configures the dialog from the task.
	 * 
	 * @param task
	 *            the task to be used to configure the dialog.
	 */
	protected void configure(Task task) {
		// The dialog cannot be made invisible by the user if the task is modal.
		closeAction.setEnabled(!task.isModal());

		// Show or hide the dialog depending on the task.
		if (task.isModal()) {
			setVisible(true);
		} else if (cbHideWhenPossible.isSelected()) {
			setVisible(false);
		}

		// Set dialog's modality.
		// TODO: seems not to work very well.
		// setModal(task.isModal());
	}

	/**
	 * This method is invoked from the task manager when the last task has finished and there is no more tasks to run. Basically, this
	 * method hides the dialog if the "Hide when possible" checkbox is selected.
	 */
	protected void hideWhenPossible() {
		closeAction.setEnabled(true);
		if (cbHideWhenPossible.isSelected()) {
			setVisible(false);
		}
	}

	/*
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		// We want the dialog to be always positioned relative to the parent frame.
		if (b && !isVisible()) {
			setLocationRelativeTo(TaskManager.getOwnerFrame());
		}
		super.setVisible(b);
	}

}