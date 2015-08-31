package org.andresoviedo.util.swing.jnotepad;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import org.andresoviedo.util.swing.jnotepad.resources.Resources;

/**
 * An action that can be used to bring up a find/replace dialog.
 * 
 */
class FindReplaceAction extends AbstractAction {

	/**
	 * The find/replace panel.
	 */
	private FindReplacePanel panel;

	/**
	 * The associated editor.
	 */
	private JTextComponent editor;

	/**
	 * Creates a new action with the associated editor.
	 * 
	 * @param editor
	 *            the associated editor.
	 */
	public FindReplaceAction(JTextComponent editor) {
		super(Resources.getString(Resources.ACTION_FIND_REPLACE));
		putValue(Action.ACTION_COMMAND_KEY, "find-replace");
		putValue(Action.SMALL_ICON, Resources.getIcon("find_text_16.png"));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
		putValue(Action.SHORT_DESCRIPTION, Resources.getString(Resources.ACTION_FIND_REPLACE_DESCRIPTION));

		this.editor = editor;
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (panel == null) {
			panel = new FindReplacePanel(editor);
		}
		panel.showDialog(editor);
	}

}
