package org.andresoviedo.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;

/**
 * A set of useful methods related to Swing.
 * 
 */
public class SwingUtils {

	private static HashMap<Font, FontMetrics> font2FM = new HashMap<Font, FontMetrics>();

	/**
	 * Don't let anyone instantiate this class.
	 */
	private SwingUtils() {
	}

	/**
	 * Sets the maximum text length in a text component, using a custom document filter.
	 * 
	 * @param component
	 *            the text component.
	 * @param maxSize
	 *            the maximum text length.
	 */
	public static void applyFixedSizeFilter(JTextComponent component, int maxSize) {
		if (component != null) {
			AbstractDocument doc = (AbstractDocument) component.getDocument();
			doc.setDocumentFilter(new FixedSizeFilter(Math.abs(maxSize)));
		}
	}

	/**
	 * Binds an action to a component. Uses the <code>Action.ACTION_COMMAND_KEY</code> and <code>Action.ACCELERATOR_KEY</code> action
	 * properties.
	 * 
	 * @param component
	 *            the component.
	 * @param action
	 *            the action to bind.
	 */
	public static void bindAction(JComponent component, Action action) {
		component.getInputMap().put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), action.getValue(Action.ACTION_COMMAND_KEY));
		component.getActionMap().put(action.getValue(Action.ACTION_COMMAND_KEY), action);
	}

	/**
	 * Binds an action to the dialog so when the user presses the ESCAPE key, the dialog is hidden.
	 * 
	 * @param dialog
	 *            the dialog to bind the action to.
	 */
	public static void bindEscapeAction(final JDialog dialog) {
		InputMap iMap = dialog.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");

		ActionMap aMap = dialog.getRootPane().getActionMap();
		aMap.put("escape", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
	}

	/**
	 * Configures a button as if it was an hyperlink.
	 * 
	 * @param button
	 *            the button to configure.
	 */
	public static void configureButtonAsHyperlink(JButton button) {
		if (button == null) {
			return;
		}

		StringBuffer html = new StringBuffer();
		html.append("<html><font color=\"blue\"><u>");
		html.append(button.getText());
		html.append("</u></font></html>");

		button.setText(html.toString());
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
	}

	/**
	 * Configures a label as if it was an hyperlink.
	 * 
	 * @param label
	 *            the label to configure.
	 */
	public static void configureLabelAsHyperlink(JLabel label) {
		if (label == null) {
			return;
		}

		StringBuffer html = new StringBuffer();
		html.append("<html><font color=\"blue\"><u>");
		html.append(label.getText());
		html.append("</u></font></html>");

		label.setText(html.toString());
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	/**
	 * Enables or disables all components in the hierarchy of <code>c</code>.
	 * 
	 * @param c
	 *            the component.
	 * @param flag
	 *            <code>true</code> if the component hierarchy has to be enabled, <code>false</code> otherwise.
	 */
	public static void enableComponentsRecursively(Component c, boolean flag) {
		c.setEnabled(flag);

		if (c instanceof Container) {
			Container container = (Container) c;
			for (int i = 0; i < container.getComponentCount(); i++) {
				enableComponentsRecursively(container.getComponent(i), flag);
			}
		}
	}

	/**
	 * Get the font metrics for the given font.
	 * 
	 * @param f
	 *            font for which the metrics is being retrieved.
	 * @param c
	 *            component that is used to retrieve the metrics in case it's not yet in the cache.
	 */
	public static FontMetrics getFontMetrics(Font f, Component c) {
		synchronized (font2FM) {
			FontMetrics fm = font2FM.get(f);
			if (fm == null) {
				fm = c.getFontMetrics(f);
				font2FM.put(f, fm);
			}
			return fm;
		}
	}

	/**
	 * Get the font metrics for the given font.
	 * 
	 * @param f
	 *            font for which the metrics is being retrieved.
	 * @param g
	 *            graphics that is used to retrieve the metrics in case it's not yet in the cache.
	 */
	public static FontMetrics getFontMetrics(Font f, Graphics g) {
		synchronized (font2FM) {
			FontMetrics fm = font2FM.get(f);
			if (fm == null) {
				fm = g.getFontMetrics(f);
				font2FM.put(f, fm);
			}
			return fm;
		}
	}

	/**
	 * Gets the internal frame which is the top level container of <code>component</code>.
	 * 
	 * @param component
	 *            an arbitrary component.
	 * @return the internal frame which is the top level container of <code>component</code>.
	 */
	public static JInternalFrame getInternalFrameForComponent(Component component) {
		if (component == null) {
			return null;
		}
		if (component instanceof JInternalFrame) {
			return (JInternalFrame) component;
		}
		return getInternalFrameForComponent(component.getParent());
	}

	/**
	 * Expands all nodes of a <code>JTree</code> component.
	 * 
	 * @param tree
	 *            the <code>JTree</code> component.
	 */
	public static void expandAll(JTree tree) {
		expandAll(tree, new TreePath(tree.getModel().getRoot()), true);
	}

	/**
	 * Collapses all nodes of a <code>JTree</code> component.
	 * 
	 * @param tree
	 *            the <code>JTree</code> component.
	 */
	public static void collapseAll(JTree tree) {
		expandAll(tree, new TreePath(tree.getModel().getRoot()), false);
	}

	/**
	 * Shows an information message dialog.
	 * 
	 * @param c
	 *            determines the Frame in which the dialog is displayed.
	 * @param msg
	 *            the message to display.
	 */
	public static void showInformationDialog(Component c, String msg) {
		JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(c), msg, UIManager.getString("OptionPane.informationDialogTitle"),
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Shows a warning message dialog.
	 * 
	 * @param c
	 *            determines the Frame in which the dialog is displayed.
	 * @param msg
	 *            the message to display.
	 */
	public static void showWarningDialog(Component c, String msg) {
		JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(c), msg, UIManager.getString("OptionPane.warningDialogTitle"),
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Shows an error message dialog.
	 * 
	 * @param c
	 *            determines the Frame in which the dialog is displayed.
	 * @param msg
	 *            the message to display.
	 */
	public static void showErrorDialog(Component c, String msg) {
		JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(c), msg, UIManager.getString("OptionPane.errorDialogTitle"),
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Shows a confirm dialog.
	 * 
	 * @param c
	 *            the parent component.
	 * @param msg
	 *            the message to display.
	 * @return an int indicating the option selected by the user.
	 */
	public static int showConfirmDialog(Component c, String msg) {
		return showConfirmDialog(c, msg, JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Shows a confirm dialog.
	 * 
	 * @param c
	 *            the parent component.
	 * @param msg
	 *            the message to display.
	 * @param messageType
	 *            the type of message to display.
	 * @return an int indicating the option selected by the user.
	 */
	public static int showConfirmDialog(Component c, String msg, int messageType) {
		return JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(c), msg,
				UIManager.getString("OptionPane.confirmDialogTitle"), JOptionPane.YES_NO_OPTION, messageType);
	}

	/**
	 * Shows an input dialog.
	 * 
	 * @param c
	 *            the parent component.
	 * @param msg
	 *            the message to display.
	 * @return the string typed by the user, or <code>null</code> if the user cancels the option pane.
	 */
	public static String showInputDialog(Component c, String msg) {
		return JOptionPane.showInputDialog(JOptionPane.getFrameForComponent(c), msg);
	}

	/**
	 * Expands or collapses the specified tree path and all its children.
	 * 
	 * @param tree
	 *            the tree component.
	 * @param parent
	 *            the tree path to expand.
	 * @param expand
	 *            <code>true</code> to expand, <code>false</code> to collapse.
	 */
	private static void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children.
		Object node = parent.getLastPathComponent();
		int childCount = tree.getModel().getChildCount(node);
		if (childCount >= 0) {
			for (int i = 0; i < childCount; i++) {
				expandAll(tree, parent.pathByAddingChild(tree.getModel().getChild(node, i)), expand);
			}
		}
		// Expansion or collapse must be done bottom-up.
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

	/**
	 * This document filter can be used to limit the number of character a text component can have.
	 */
	public static class FixedSizeFilter extends DocumentFilter {

		/**
		 * Maximum text length.
		 */
		private int maxSize;

		/**
		 * Creates a new fixed-sized filter.
		 * 
		 * @param maxSize
		 *            the maximum text length.
		 */
		public FixedSizeFilter(int maxSize) {
			this.maxSize = maxSize;
		}

		/**
		 * This method is called when characters are inserted into the document.
		 * 
		 * @see javax.swing.text.DocumentFilter#insertString(javax.swing.text.DocumentFilter.FilterBypass, int, java.lang.String,
		 *      javax.swing.text.AttributeSet)
		 */
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String str, AttributeSet attr) throws BadLocationException {
			replace(fb, offset, 0, str, attr);
		}

		/**
		 * This method is called when characters in the document are replaced with other characters.
		 * 
		 * @see javax.swing.text.DocumentFilter#replace(javax.swing.text.DocumentFilter.FilterBypass, int, int, java.lang.String,
		 *      javax.swing.text.AttributeSet)
		 */
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet attrs)
				throws BadLocationException {
			int newLength = fb.getDocument().getLength() - length + str.length();
			if (newLength <= maxSize) {
				fb.replace(offset, length, str, attrs);
			}
		}

	}

}