package org.andresoviedo.util.swing.jchecktree;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {

	private CheckTreeSelectionModel selectionModel;
	private TreeCellRenderer delegate;
	private TristateCheckBox checkBox;

	public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel) {
		this.delegate = delegate;
		this.selectionModel = selectionModel;

		checkBox = new TristateCheckBox();
		checkBox.setOpaque(false);

		setLayout(new BorderLayout());
		setOpaque(false);
		add(checkBox, BorderLayout.WEST);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		TreePath path = tree.getPathForRow(row);
		if (path != null) {
			if (selectionModel.isPathSelected(path, true)) {
				checkBox.setState(Boolean.TRUE);
			} else {
				checkBox.setState(selectionModel.isPartiallySelected(path) ? null : Boolean.FALSE);
			}
			checkBox.setEnabled(renderer.isEnabled());
		}

		// Preserve original renderer's tooltip, if any.
		if (renderer instanceof JComponent) {
			setToolTipText(((JComponent) renderer).getToolTipText());
		}

		add(renderer, BorderLayout.CENTER);
		return this;
	}
}
