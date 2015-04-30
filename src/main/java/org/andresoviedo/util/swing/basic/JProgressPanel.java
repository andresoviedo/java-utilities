package org.andresoviedo.util.swing.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class JProgressPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The label used to display a message.
	 */
	private JLabel lblMessage;

	/**
	 * The progress bar to show the progress of a task.
	 */
	private JProgressBar pbProgress;

	/**
	 * The number of steps (see nextStep method).
	 */
	private int steps;

	public JProgressPanel(int steps) {
		this.steps = steps;
		try {
			installComponents();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void installComponents() {
		setLayout(new GridLayout(0, 1, 5, 5));
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		lblMessage = new JLabel("");

		pbProgress = new JProgressBar(0, 100);
		pbProgress.setMinimumSize(new Dimension(500, 15));
		pbProgress.setPreferredSize(new Dimension(500, 15));

		add(lblMessage);
		add(pbProgress);
	}

	public void setMessage(String text) {
		lblMessage.setText(text);
	}

	public void setProgressValue(int value) {
		pbProgress.setValue(value);
	}

	public void nextStep(String text) {
		setMessage(text);
		setProgressValue(pbProgress.getValue() + (100 / steps));
	}

}
