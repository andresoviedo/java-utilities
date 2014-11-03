package org.andresoviedo.util.swing.jtaskmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.andresoviedo.util.swing.jtaskmanager.resources.Resources;


/**
 * This class is used to display the progress of the associated task.
 * 

 */
public class TaskPanel extends JPanel implements ActionListener, ItemListener, PropertyChangeListener {

	/**
	 * The associated task.
	 */
	private Task task;

	private JLabel lblTaskDescription;

	private JLabel lblTaskMessage;

	private JProgressBar pbProgress;

	private JButton btnCancel;

	private JToggleButton btnDetails;

	private JTextPane tpDetails;

	private JComponent centerComponent;

	/**
	 * Constructs a new task panel.
	 * 
	 * @param task
	 *          the associated task.
	 */
	public TaskPanel(Task task) {
		if (task == null) {
			throw new IllegalArgumentException("The task cannot be null.");
		}
		this.task = task;
		try {
			installComponents();
			installListeners();
		} catch (Exception e) {
		}
	}

	/**
	 * Installs GUI components.
	 */
	private void installComponents() {
		setLayout(new BorderLayout());
		add(createNorthPanel(), BorderLayout.NORTH);
		add(centerComponent = createCenterPanel(), BorderLayout.CENTER);
	}

	private JComponent createNorthPanel() {
		lblTaskDescription = new JLabel(task.getDescription());
		lblTaskDescription.setFont(lblTaskDescription.getFont().deriveFont(Font.BOLD));
		lblTaskDescription.setIcon(task.getIcon());
		lblTaskDescription.setPreferredSize(new Dimension(100, 15));

		pbProgress = new JProgressBar(0, 100);
		pbProgress.setValue(task.getProgress());
		pbProgress.setIndeterminate(task.isIndeterminate());
		pbProgress.setPreferredSize(new Dimension(100, 15));

		lblTaskMessage = new JLabel(task.getMessage());
		lblTaskMessage.setPreferredSize(new Dimension(100, 15));

		btnCancel = new JButton(task.isCanCancel() ? Resources.getIcon("media_stop_red_16.png") : Resources.getIcon("forbidden_16.png"));
		btnCancel.setPreferredSize(new Dimension(24, 24));
		btnCancel.setToolTipText(Resources.getString(Resources.TASK_MANAGER_TOOLTIP_CANCEL));
		btnCancel.setEnabled(task.isCanCancel());

		btnDetails = new JToggleButton(Resources.getIcon("document_16.png"));
		btnDetails.setPreferredSize(new Dimension(24, 24));
		btnDetails.setToolTipText(Resources.getString(Resources.TASK_MANAGER_TOOLTIP_DETAILS));

		JPanel p = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 0);

		p.add(lblTaskDescription, gbc);

		gbc.gridy = GridBagConstraints.RELATIVE;

		p.add(pbProgress, gbc);

		gbc.gridy = GridBagConstraints.RELATIVE;

		p.add(lblTaskMessage, gbc);

		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 3;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 5, 0, 5);

		p.add(btnCancel, gbc);

		gbc.gridx++;
		gbc.insets = new Insets(0, 0, 0, 0);

		p.add(btnDetails, gbc);

		return p;
	}

	private JComponent createCenterPanel() {
		tpDetails = new JTextPane();
		tpDetails.setEditable(false);

		JScrollPane sp = new JScrollPane(tpDetails);
		sp.setPreferredSize(new Dimension(100, 100));
		sp.setVisible(false);

		return sp;
	}

	/**
	 * Installs listeners as needed.
	 */
	private void installListeners() {
		btnCancel.addActionListener(this);
		btnDetails.addItemListener(this);
		task.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	/**
	 * Gets the associated task.
	 * 
	 * @return the associated task.
	 */
	public Task getTask() {
		return task;
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			// First of all, disable the button to avoid cancelling multiple times.
			btnCancel.setEnabled(false);
			// Cancel the task.
			task.cancel();
		}
	}

	/*
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		centerComponent.setVisible(btnDetails.isSelected());
		revalidate();
	}

	/*
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(final PropertyChangeEvent e) {
		// Notice that this method will probably never be executed in the event-dispatching thread, so we need to use invokeLater.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (e.getPropertyName() == Task.DESCRIPTION_PROPERTY) {
					lblTaskDescription.setText(e.getNewValue().toString());
				} else if (e.getPropertyName() == Task.ICON_PROPERTY) {
					lblTaskDescription.setIcon((Icon) e.getNewValue());
				} else if (e.getPropertyName() == Task.MESSAGE_PROPERTY) {
					lblTaskMessage.setText(e.getNewValue().toString());
					Document doc = tpDetails.getDocument();
					try {
						if (doc.getLength() > 0) {
							doc.insertString(doc.getLength(), System.getProperty("line.separator"), null);
						}
						doc.insertString(doc.getLength(), e.getNewValue().toString(), null);
					} catch (BadLocationException ex) {
					}
				} else if (e.getPropertyName() == Task.PROGRESS_PROPERTY) {
					pbProgress.setValue(((Integer) e.getNewValue()).intValue());
					btnCancel.setEnabled(task.isCanCancel() && (task.getProgress() < 100));
				} else if (e.getPropertyName() == Task.INDETERMINATE_PROPERTY) {
					pbProgress.setIndeterminate(((Boolean) e.getNewValue()).booleanValue());
				}
			}
		});
	}

}