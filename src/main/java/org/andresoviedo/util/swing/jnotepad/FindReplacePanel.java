package org.andresoviedo.util.swing.jnotepad;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.andresoviedo.util.swing.SwingUtils;
import org.andresoviedo.util.swing.jnotepad.resources.Resources;


/**
 * Find/Replace panel.
 * 

 */
public class FindReplacePanel extends JPanel implements FindReplaceOptions, ActionListener, ItemListener, DocumentListener {

	private static final int MAX_ELEMENTS = 10;

	private JComboBox cboText;
	private JComboBox cboReplace;

	private JRadioButton rbForward;
	private JRadioButton rbBackward;

	private JRadioButton rbAll;
	private JRadioButton rbSelectedLines;

	private JCheckBox cbCaseSensitive;
	private JCheckBox cbMatchWholeWord;
	private JCheckBox cbUseRegex;

	private JButton btnFind;
	private JButton btnReplace;
	private JButton btnReplaceAll;
	private JButton btnReplaceFind;

	/**
	 * The command executor.
	 */
	private FindReplaceCommandExecutor executor;

	/**
	 * Creates a find/replace panel.
	 */
	public FindReplacePanel(JTextComponent editor) {
		this.executor = new FindReplaceBasicCommandExecutor(editor, this);
		try {
			installComponents();
			installListeners();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FindReplaceCommandExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(FindReplaceCommandExecutor executor) {
		this.executor = executor;
	}

	/**
	 * Installs GUI components.
	 */
	private void installComponents() {
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 5, 0);

		// Text panel.
		gbc.gridwidth = 2;

		add(createTextPanel(), gbc);

		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;

		add(createDirectionPanel(), gbc);

		gbc.gridx++;

		add(createScopePanel(), gbc);

		//
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.weightx = 1;

		add(createOptionsPanel(), gbc);

		gbc.gridy++;

		add(createButtonPanel(), gbc);
	}

	/**
	 * Installs listeners as needed.
	 */
	private void installListeners() {
		cboText.addActionListener(this);

		Component c = cboText.getEditor().getEditorComponent();
		if (c instanceof JTextComponent) {
			((JTextComponent) c).getDocument().addDocumentListener(this);
		}

		btnFind.addActionListener(this);
		btnReplace.addActionListener(this);
		btnReplaceAll.addActionListener(this);
		btnReplaceFind.addActionListener(this);

		rbAll.addActionListener(this);
		rbSelectedLines.addActionListener(this);

		cbUseRegex.addItemListener(this);
	}

	private JComponent createTextPanel() {
		cboText = new JComboBox();
		cboText.setEditable(true);
		cboText.setPreferredSize(new Dimension(200, 20));

		cboReplace = new JComboBox();
		cboReplace.setEditable(true);
		cboReplace.setPreferredSize(new Dimension(200, 20));

		JPanel p = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(0, 0, 5, 0);

		// Files to look for.
		p.add(new JLabel(Resources.getString(Resources.LABEL_FIND)), gbc);

		gbc.gridx++;

		p.add(cboText, gbc);

		// 
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);

		p.add(new JLabel(Resources.getString(Resources.LABEL_REPLACE_WITH)), gbc);

		gbc.gridx++;

		p.add(cboReplace, gbc);

		return p;
	}

	private JComponent createDirectionPanel() {
		rbForward = new JRadioButton(Resources.getString(Resources.OPTION_DIRECTION_FORWARD), true);
		rbBackward = new JRadioButton(Resources.getString(Resources.OPTION_DIRECTION_BACKWARD));

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbForward);
		bg.add(rbBackward);

		JPanel p = new JPanel(new GridLayout(0, 1));
		p.setBorder(BorderFactory.createTitledBorder(Resources.getString(Resources.TITLE_DIRECTION)));
		p.add(rbForward);
		p.add(rbBackward);

		return p;
	}

	private JComponent createScopePanel() {
		rbAll = new JRadioButton(Resources.getString(Resources.OPTION_SCOPE_ALL), true);
		rbAll.setEnabled(false);

		rbSelectedLines = new JRadioButton(Resources.getString(Resources.OPTION_SCOPE_SELECTED_LINES));
		rbSelectedLines.setEnabled(false);

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbAll);
		bg.add(rbSelectedLines);

		JPanel p = new JPanel(new GridLayout(0, 1));
		p.setBorder(BorderFactory.createTitledBorder(Resources.getString(Resources.TITLE_SCOPE)));
		p.add(rbAll);
		p.add(rbSelectedLines);

		return p;
	}

	private JComponent createOptionsPanel() {
		cbCaseSensitive = new JCheckBox(Resources.getString(Resources.OPTION_CASE_SENSITIVE));
		cbMatchWholeWord = new JCheckBox(Resources.getString(Resources.OPTION_MATCH_WHOLE_WORD));
		cbUseRegex = new JCheckBox(Resources.getString(Resources.OPTION_USE_REGEX));

		JPanel p = new JPanel(new GridLayout(0, 1));
		p.setBorder(BorderFactory.createTitledBorder(Resources.getString(Resources.OPTION_CASE_SENSITIVE)));
		p.add(cbCaseSensitive);
		p.add(cbMatchWholeWord);
		p.add(cbUseRegex);

		return p;
	}

	private JComponent createButtonPanel() {
		btnFind = new JButton(Resources.getString(Resources.ACTION_FIND));
		btnFind.setEnabled(false);

		btnReplace = new JButton(Resources.getString(Resources.ACTION_REPLACE));
		btnReplace.setEnabled(false);

		btnReplaceAll = new JButton(Resources.getString(Resources.ACTION_REPLACE_ALL));
		btnReplaceAll.setEnabled(false);

		btnReplaceFind = new JButton(Resources.getString(Resources.ACTION_REPLACE_FIND));
		btnReplaceFind.setEnabled(false);

		JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
		p.add(btnFind);
		p.add(btnReplaceFind);
		p.add(btnReplace);
		p.add(btnReplaceAll);

		return p;
	}

	private void updateUserChoice(JComboBox comboBox) {
		Object item = comboBox.getSelectedItem();
		if (item == null) {
			return;
		}

		if (!(comboBox.getModel() instanceof MutableComboBoxModel)) {
			return;
		}

		MutableComboBoxModel model = (MutableComboBoxModel) comboBox.getModel();

		int index = comboBox.getSelectedIndex();
		if (index > 0) {
			// Move the item.
			model.removeElementAt(index);
			model.insertElementAt(item, 0);
		} else if ((index < 0) && (item.toString().length() > 0)) {
			// Check the maximum number of items allowed.
			if (model.getSize() == MAX_ELEMENTS) {
				model.removeElementAt(model.getSize() - 1);
			}
			// Add the item.
			model.insertElementAt(item, 0);
		}
		comboBox.setSelectedItem(item);
	}

	private void checkButtonsState(boolean b) {
		String text = "";
		Component c = cboText.getEditor().getEditorComponent();
		if (c instanceof JTextComponent) {
			text = ((JTextComponent) c).getText();
		}
		int index = cboText.getSelectedIndex();

		btnFind.setEnabled((index >= 0) || (text.length() > 0));
		btnReplaceAll.setEnabled(btnFind.isEnabled());
		if (b) {
			btnReplace.setEnabled(false);
			btnReplaceFind.setEnabled(false);
		}
	}

	private void doFind() {
		updateUserChoice(cboText);
		if (executor.find()) {
			btnReplace.setEnabled(true);
			btnReplaceFind.setEnabled(true);
		} else {
			SwingUtils.showInformationDialog(this, Resources.getString(Resources.MESSAGE_STRING_NOT_FOUND));
		}
	}

	private void doReplace() {
		executor.replace();
		updateUserChoice(cboReplace);
	}

	private void doReplaceAll() {
		updateUserChoice(cboReplace);
		int result = executor.replaceAll();
		if (result == 0) {
			SwingUtils.showInformationDialog(this, Resources.getString(Resources.MESSAGE_STRING_NOT_FOUND));
		} else {
			SwingUtils.showInformationDialog(this, Resources.getMessage(Resources.PATTERN_X_OCURRENCES_REPLACED, String.valueOf(result)));
		}
	}

	private void doReplaceFind() {
		updateUserChoice(cboReplace);
		if (executor.replaceFind()) {
			btnReplace.setEnabled(true);
			btnReplaceFind.setEnabled(true);
		} else {
			SwingUtils.showInformationDialog(this, Resources.getString(Resources.MESSAGE_NO_MORE_STRINGS_FOUND));
		}
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptionsProvider#getDirection()
	 */
	public int getDirection() {
		return rbForward.isSelected() ? FindReplaceOptions.DIRECTION_FORWARD : FindReplaceOptions.DIRECTION_BACKWARD;
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptions#getReplaceText()
	 */
	public String getReplaceText() {
		return (cboReplace.getSelectedItem() == null) ? "" : cboReplace.getSelectedItem().toString();
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptions#getScope()
	 */
	public int getScope() {
		return rbAll.isSelected() ? FindReplaceOptions.SCOPE_ALL : FindReplaceOptions.SCOPE_SELECTED_LINES;
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptions#setScope(int)
	 */
	public void setScope(int scope) {
		if (scope == FindReplaceOptions.SCOPE_SELECTED_LINES) {
			rbSelectedLines.setSelected(true);
		} else {
			rbAll.setSelected(true);
		}
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptions#getText()
	 */
	public String getText() {
		return (cboText.getSelectedItem() == null) ? "" : cboText.getSelectedItem().toString();
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptions#isCaseSensitive()
	 */
	public boolean isCaseSensitive() {
		return cbCaseSensitive.isSelected();
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptions#isMatchWholeWord()
	 */
	public boolean isMatchWholeWord() {
		return cbMatchWholeWord.isSelected();
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptions#isUseRegex()
	 */
	public boolean isUseRegex() {
		return cbUseRegex.isSelected();
	}

	/*
	 * @see ttm.tools.bootstraploader.gui.textsearch.FindTextOptions#disableReplace()
	 */
	public void disableReplace() {
		btnReplace.setEnabled(false);
		btnReplaceFind.setEnabled(false);
	}

	/*
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cboText) {
			checkButtonsState(false);
		} else if (e.getSource() == btnFind) {
			doFind();
		} else if (e.getSource() == btnReplace) {
			doReplace();
		} else if (e.getSource() == btnReplaceAll) {
			doReplaceAll();
		} else if (e.getSource() == btnReplaceFind) {
			doReplaceFind();
		} else if (e.getSource() == rbAll || e.getSource() == rbSelectedLines) {
			// executor.scopeChanged();
		}
	}

	/*
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		cbMatchWholeWord.setEnabled(!cbUseRegex.isSelected());
	}

	/*
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent e) {
	}

	/*
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent e) {
		checkButtonsState(true);
	}

	/*
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e) {
		checkButtonsState(true);
	}

	public void showDialog(Component component) {
		Window window = SwingUtilities.getWindowAncestor(this);
		if (window != null) {
			window.setVisible(true);
		} else {
			JOptionPane op = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE);
			JDialog dialog = op.createDialog(component, Resources.getString(Resources.TITLE_FIND_REPLACE));
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setModal(false);
			dialog.pack();
			dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
			dialog.setVisible(true);
		}
	}

}