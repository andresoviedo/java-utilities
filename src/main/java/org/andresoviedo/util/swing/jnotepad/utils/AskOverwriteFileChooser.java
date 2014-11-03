package org.andresoviedo.util.swing.jnotepad.utils;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.andresoviedo.util.swing.SwingUtils;
import org.andresoviedo.util.swing.jnotepad.resources.Resources;

/**
 * <code>JFileChooser</code> asking the user what to do when a file has to be overwritten.
 */
public class AskOverwriteFileChooser extends JFileChooser {

	private JDialog myDialog;

	private int myReturnValue;

	public AskOverwriteFileChooser() {
		super();
	}

	public AskOverwriteFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}

	public AskOverwriteFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	public AskOverwriteFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	public AskOverwriteFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	public AskOverwriteFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	/*
	 * @see javax.swing.JFileChooser#showSaveDialog(java.awt.Component)
	 */
	public int showSaveDialog(Component parent) throws HeadlessException {
		setDialogType(SAVE_DIALOG);

		myDialog = createDialog(parent);

		// Add listener for approve and cancel events
		addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
					File f = getSelectedFile();
					if (f.exists()) {
						if (SwingUtils.showConfirmDialog(AskOverwriteFileChooser.this, Resources.getString(Resources.QUESTION_FILE_EXISTS_OVERWRITE),
								JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
							myReturnValue = APPROVE_OPTION;
							myDialog.setVisible(false);
						}
					} else {
						myReturnValue = APPROVE_OPTION;
						myDialog.setVisible(false);
					}
				} else if (JFileChooser.CANCEL_SELECTION.equals(e.getActionCommand())) {
					myDialog.setVisible(false);
				}
			}
		});

		// Add listener for window closing events.
		myDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myReturnValue = CANCEL_OPTION;
				myDialog.setVisible(false);
			}
		});

		myReturnValue = ERROR_OPTION;
		rescanCurrentDirectory();

		myDialog.setVisible(true);
		myDialog.dispose();
		myDialog = null;

		return myReturnValue;
	}

}
