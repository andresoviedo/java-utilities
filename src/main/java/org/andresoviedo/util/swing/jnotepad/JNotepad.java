package org.andresoviedo.util.swing.jnotepad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.andresoviedo.util.encoding.UnicodeInputStream;
import org.andresoviedo.util.swing.SwingUtils;
import org.andresoviedo.util.swing.jnotepad.resources.Resources;
import org.andresoviedo.util.swing.jnotepad.utils.AskOverwriteFileChooser;

/**
 * A notepad component to load, save, and edit text files.
 * 

 */
public class JNotepad extends JPanel implements NotepadActions, MouseListener, UndoableEditListener, FocusListener {

	/**
	 * The preferred size for toolbar buttons. This size assumes that no text is displayed, only an icon.
	 */
	private static final Dimension BUTTON_SIZE = new Dimension(28, 28);

	/**
	 * UndoManager that we add edits to.
	 */
	private UndoManager undo = new UndoManager();

	/**
	 * The text component used to represent the given document.
	 */
	private JTextComponent editor;

	/**
	 * The toolbar.
	 */
	private JToolBar toolbar;

	/**
	 * A component used as a status panel.
	 */
	private JComponent status;

	/**
	 * The popup menu.
	 */
	private JPopupMenu popup;

	private Action newAction;
	private Action openAction;
	private Action saveAction;
	private Action saveAsAction;
	private UndoAction undoAction;
	private RedoAction redoAction;
	private Action findReplaceAction;
	private Action cutAction;
	private Action copyAction;
	private Action pasteAction;
	private Action gotoLineAction;

	private JDialog dialog;

	/**
	 * The file loader (a reference is needed to interrupt the current load if the user wants to).
	 */
	private FileLoader loader;

	/**
	 * A reference to the last opened file.
	 */
	private File lastOpenedFile;

	/**
	 * The time the currently opened file was last modified.
	 */
	private long lastOpenedFileModified;

	/**
	 * A reference to the last saved file.
	 */
	private File lastSavedFile;

	/**
	 * Control flag for the reload file feature.
	 */
	private boolean ignoreNextFocusEvent = false;

	/**
	 * Indicates whether this notepad will attempt to set the main window's title based on the opened file.
	 */
	private boolean autoTitleWindow = false;

	/**
	 * The action set used to construct the toolbar and the popup menu.
	 */
	private NotepadActionSet actionSet;

	/**
	 * The list of custom highlights.
	 */
	private List<Object> highlights = new Vector<Object>();

	/**
	 * The listener list.
	 */
	private EventListenerList listenerList;

	/**
	 * Creates a new notepad.
	 */
	public JNotepad() {
		this(new NotepadActionSet());
	}

	/**
	 * Creates a new notepad with the specified action set.
	 * 
	 * @param actionSet
	 *          the action set.
	 */
	public JNotepad(NotepadActionSet actionSet) {
		this(actionSet, true, true);
	}

	/**
	 * Creates a new notepad, indicating whether to show the toolbar and the popup menu.
	 * 
	 * @param showToolbar
	 *          <code>true</code> if the toolbar has to be shown.
	 * @param showPopup
	 *          <code>true</code> if the popup menu has to be shown.
	 */
	public JNotepad(boolean showToolbar, boolean showPopup) {
		this(new NotepadActionSet(), showToolbar, showPopup);
	}

	public JNotepad(NotepadActionSet actionSet, boolean showToolbar, boolean showPopup) {
		this.actionSet = actionSet;
		this.listenerList = new EventListenerList();
		this.editor = createEditor();
		this.createActions();

		if (showPopup) {
			createPopupMenu();
		}

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(800, 600));
		if (showToolbar) {
			add(createToolbar(), BorderLayout.NORTH);
		}
		add(createEditorPanel(), BorderLayout.CENTER);
		add(createStatusbar(), BorderLayout.SOUTH);

		if (showPopup) {
			editor.addMouseListener(this);
		}
		editor.addFocusListener(this);
		editor.getDocument().addUndoableEditListener(this);

		if (actionSet.isVisible(NotepadActionSet.ACTION_UNDO)) {
			SwingUtils.bindAction(editor, undoAction);
		}
		if (actionSet.isVisible(NotepadActionSet.ACTION_REDO)) {
			SwingUtils.bindAction(editor, redoAction);
		}
		if (actionSet.isVisible(NotepadActionSet.ACTION_FIND_REPLACE)) {
			SwingUtils.bindAction(editor, findReplaceAction);
		}
		if (actionSet.isVisible(NotepadActionSet.ACTION_GOTO_LINE)) {
			SwingUtils.bindAction(editor, gotoLineAction);
		}
	}

	/**
	 * Creates the editor panel.
	 * 
	 * @return the editor panel.
	 */
	private Component createEditorPanel() {
		JScrollPane sp = new JScrollPane(getEditor());
		sp.setRowHeaderView(new NotepadGlyphGutter(this));

		return sp;
	}

	/**
	 * Create an editor to represent the given document. This method can be overwritten to create a custom editor.
	 * 
	 * @return an editor to represent the given document.
	 */
	protected JTextComponent createEditor() {
		JTextComponent c = new JTextArea();
		c.setDragEnabled(true);
		c.setFont(new Font("monospaced", Font.PLAIN, 12));
		return c;
	}

	/**
	 * Creates a document for the editor used when a file is loaded or a new file is created.
	 * 
	 * @return a document for the editor used when a file is loaded.
	 */
	protected Document createDocument() {
		return new PlainDocument();
	}

	/**
	 * Creates the actions.
	 */
	private void createActions() {
		newAction = new NewFileAction();
		openAction = new OpenAction();
		saveAction = new SaveAction();
		saveAsAction = new SaveAsAction();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		findReplaceAction = new FindReplaceAction(getEditor());
		gotoLineAction = new GotoLineAction();

		// Get a reference to the cut/copy/paste actions.
		Action a = null;
		Action[] actionsArray = getEditor().getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			a = actionsArray[i];
			if (DefaultEditorKit.cutAction.equals(a.getValue(Action.NAME))) {
				cutAction = actionsArray[i];
			} else if (DefaultEditorKit.copyAction.equals(a.getValue(Action.NAME))) {
				copyAction = actionsArray[i];
			} else if (DefaultEditorKit.pasteAction.equals(a.getValue(Action.NAME))) {
				pasteAction = actionsArray[i];
			}
		}
	}

	/**
	 * Creates a toolbar for this notepad. This method may be overwritten to create a custom toolbar.
	 */
	protected Component createToolbar() {
		JButton btn;
		boolean separator = false;

		toolbar = new JToolBar();
		toolbar.setFloatable(false);

		// New action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_NEW)) {
			toolbar.add(createToolBarButton(newAction));
			separator = true;
		}
		// Open action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_OPEN)) {
			toolbar.add(createToolBarButton(openAction));
			separator = true;
		}
		// Save action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_SAVE)) {
			toolbar.add(createToolBarButton(saveAction));
			separator = true;
		}
		// Save as action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_SAVE_AS)) {
			toolbar.add(createToolBarButton(saveAsAction));
			separator = true;
		}

		if (separator) {
			toolbar.addSeparator();
			separator = false;
		}

		// Cut action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_CUT)) {
			btn = createToolBarButton(cutAction);
			btn.setToolTipText(Resources.getString(Resources.ACTION_CUT_DESCRIPTION));
			btn.setIcon(Resources.getIcon("cut_16.png"));
			toolbar.add(btn);
			separator = true;
		}
		// Copy action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_COPY)) {
			btn = createToolBarButton(copyAction);
			btn.setToolTipText(Resources.getString(Resources.ACTION_COPY_DESCRIPTION));
			btn.setIcon(Resources.getIcon("copy_16.png"));
			toolbar.add(btn);
			separator = true;
		}
		// Paste action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_PASTE)) {
			btn = createToolBarButton(pasteAction);
			btn.setToolTipText(Resources.getString(Resources.ACTION_PASTE_DESCRIPTION));
			btn.setIcon(Resources.getIcon("paste_16.png"));

			toolbar.add(btn);
		}

		if (separator) {
			toolbar.addSeparator();
			separator = false;
		}

		// Undo action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_UNDO)) {
			toolbar.add(createToolBarButton(undoAction));
			separator = true;
		}
		// Redo action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_REDO)) {
			toolbar.add(createToolBarButton(redoAction));
			separator = true;
		}

		if (separator) {
			toolbar.addSeparator();
			separator = false;
		}

		// Find/replace action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_FIND_REPLACE)) {
			toolbar.add(createToolBarButton(findReplaceAction));
		}

		return toolbar;
	}

	/**
	 * Creates a button with the associated action to be used in the toolbar.
	 */
	protected JButton createToolBarButton(Action action) {
		JButton btn = new JButton(action);
		btn.setText("");
		btn.setPreferredSize(BUTTON_SIZE);
		btn.setMaximumSize(BUTTON_SIZE);

		return btn;
	}

	/**
	 * Creates a popup menu for the editor component.
	 * 
	 * @return a popup menu for the editor component.
	 */
	protected Component createPopupMenu() {
		JMenuItem mi;
		boolean separator = false;

		popup = new JPopupMenu();

		// New action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_NEW)) {
			popup.add(newAction);
			separator = true;
		}
		// Open action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_OPEN)) {
			popup.add(openAction);
			separator = true;
		}
		// Save action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_SAVE)) {
			popup.add(saveAction);
			separator = true;
		}
		// Save as.
		if (actionSet.isVisible(NotepadActionSet.ACTION_SAVE_AS)) {
			popup.add(saveAsAction);
			separator = true;
		}

		if (separator) {
			popup.addSeparator();
			separator = false;
		}

		// Cut action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_CUT)) {
			mi = popup.add(cutAction);
			mi.setText(Resources.getString(Resources.ACTION_CUT));
			mi.setToolTipText(Resources.getString(Resources.ACTION_CUT_DESCRIPTION));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
			mi.setIcon(Resources.getIcon("cut_16.png"));
			separator = true;
		}
		// Copy action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_COPY)) {
			mi = popup.add(copyAction);
			mi.setText(Resources.getString(Resources.ACTION_COPY));
			mi.setToolTipText(Resources.getString(Resources.ACTION_COPY_DESCRIPTION));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
			mi.setIcon(Resources.getIcon("copy_16.png"));
			separator = true;
		}
		// Paste action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_PASTE)) {
			mi = popup.add(pasteAction);
			mi.setText(Resources.getString(Resources.ACTION_PASTE));
			mi.setToolTipText(Resources.getString(Resources.ACTION_PASTE_DESCRIPTION));
			mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
			mi.setIcon(Resources.getIcon("paste_16.png"));
			separator = true;
		}

		if (separator) {
			popup.addSeparator();
			separator = false;
		}

		// Undo action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_UNDO)) {
			popup.add(undoAction);
			separator = true;
		}
		// Redo action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_REDO)) {
			popup.add(redoAction);
			separator = true;
		}

		if (separator) {
			popup.addSeparator();
			separator = false;
		}

		// Find/Replace action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_FIND_REPLACE)) {
			popup.add(findReplaceAction);
			separator = true;
		}

		if (separator) {
			popup.addSeparator();
		}

		// Goto line action.
		if (actionSet.isVisible(NotepadActionSet.ACTION_GOTO_LINE)) {
			popup.add(gotoLineAction);
		}

		return popup;
	}

	/**
	 * Creates a menubar for the notepad, which can be displayed as the menu bar for a <code>JFrame</code>.
	 * 
	 * @return a menubar for the notepad.
	 */
	protected JMenuBar createMenubar() {
		JMenu m;
		JMenuItem mi;
		JMenuBar mb = new JMenuBar();

		// 'File' menu.
		m = new JMenu(Resources.getString(Resources.M_FILE));
		m.setMnemonic(Resources.getChar(Resources.M_FILE_MNE));

		m.add(newAction);
		m.add(openAction);
		m.add(saveAction);
		m.add(saveAsAction);

		mb.add(m);

		// 'Edit' menu.
		m = new JMenu(Resources.getString(Resources.M_EDIT));
		m.setMnemonic(Resources.getChar(Resources.M_EDIT_MNE));

		mi = m.add(cutAction);
		mi.setText(Resources.getString(Resources.ACTION_CUT));
		mi.setIcon(Resources.getIcon("cut_16.png"));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));

		mi = m.add(copyAction);
		mi.setText(Resources.getString(Resources.ACTION_COPY));
		mi.setIcon(Resources.getIcon("copy_16.png"));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));

		mi = m.add(pasteAction);
		mi.setText(Resources.getString(Resources.ACTION_PASTE));
		mi.setIcon(Resources.getIcon("paste_16.png"));
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));

		m.addSeparator();

		m.add(undoAction);
		m.add(redoAction);

		mb.add(m);

		return mb;
	}

	/**
	 * Creates a status bar (only shown when loading or saving a file).
	 */
	protected Component createStatusbar() {
		status = new JPanel();
		status.setLayout(new BoxLayout(status, BoxLayout.X_AXIS));
		return status;
	}

	/**
	 * Adds a notepad listener to the listener list.
	 * 
	 * @param l
	 *          the listener to add.
	 */
	public void addNotepadListener(NotepadListener l) {
		listenerList.add(NotepadListener.class, l);
	}

	/**
	 * Removes a notepad listener from the listener list.
	 * 
	 * @param l
	 *          the listener to remove.
	 */
	public void removeNotepadListener(NotepadListener l) {
		listenerList.remove(NotepadListener.class, l);
	}

	/**
	 * Sends a file opened event to all registered listeners.
	 */
	protected void fireFileOpened() {
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying those that are interested in this event.
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == NotepadListener.class) {
				((NotepadListener) listeners[i + 1]).fileOpened();
			}
		}
	}

	/**
	 * Returns this notepad's editor component.
	 * 
	 * @return the notepad's editor component.
	 */
	public JTextComponent getEditor() {
		return editor;
	}

	/**
	 * Returns this notepad's toolbar.
	 * 
	 * @return the notepad's toolbar.
	 */
	public JToolBar getToolBar() {
		return toolbar;
	}

	/**
	 * Returns the action with the specified code, as specified in the <code>NotepadActions</code> interface.
	 * 
	 * @param code
	 *          the action code, as specified in the <code>NotepadActions</code> interface.
	 * @return the action with the specified code, or <code>null</code> if no action with that code is found.
	 */
	public Action getAction(short code) {
		switch (code) {
		case ACTION_NEW:
			return newAction;
		case ACTION_OPEN:
			return openAction;
		case ACTION_SAVE:
			return saveAction;
		case ACTION_SAVE_AS:
			return saveAsAction;
		case ACTION_CUT:
			return cutAction;
		case ACTION_COPY:
			return copyAction;
		case ACTION_PASTE:
			return pasteAction;
		case ACTION_UNDO:
			return undoAction;
		case ACTION_REDO:
			return redoAction;
		case ACTION_FIND_REPLACE:
			return findReplaceAction;
		case ACTION_GOTO_LINE:
			return gotoLineAction;
		default:
			return null;
		}
	}

	/**
	 * Returns <code>true</code> if the notepad has to set the title of its window ancestor automatically.
	 * 
	 * @return <code>true</code> if the notepad has to set the title of its window ancestor automatically, <code>false</code> otherwise.
	 */
	public boolean isAutoTitleWindow() {
		return autoTitleWindow;
	}

	/**
	 * Sets whether the notepad has to set the title of its window ancestor automatically.
	 * 
	 * @param autoTitleWindow
	 *          <code>true</code> if the notepad has to set the title of its window ancestor automatically, <code>false</code> otherwise.
	 */
	public void setAutoTitleWindow(boolean autoTitleWindow) {
		this.autoTitleWindow = autoTitleWindow;
	}

	/**
	 * A shorthand method to know if notepad's editor is editable. This method is equivalent to <code>getEditor().isEditable()</code>.
	 * 
	 * @return <code>true</code> if notepad's editor is editable, <code>false</code> otherwise.
	 */
	public boolean isEditable() {
		return getEditor().isEditable();
	}

	/**
	 * A shorthand method to make notepad's editor editable or uneditable. This method is equivalent to
	 * <code>getEditor().setEditable(boolean)</code>.
	 * 
	 * @param editable
	 *          <code>true</code> if notepad's editor has to be editable, <code>false</code> otherwise.
	 */
	public void setEditable(boolean editable) {
		getEditor().setEditable(editable);
	}

	/**
	 * Returns the window we're currently in.
	 * 
	 * @return the window we're currently in.
	 */
	private Window getWindow() {
		return SwingUtilities.getWindowAncestor(this);
	}

	/**
	 * Resets the undo manager.
	 */
	private void resetUndoManager() {
		undo.discardAllEdits();
		undoAction.update();
		redoAction.update();
	}

	/**
	 * Creates a new file.
	 */
	public void newFile() {
		Document old = editor.getDocument();
		if (old != null) {
			old.removeUndoableEditListener(this);
		}
		getEditor().setDocument(createDocument());
		getEditor().getDocument().addUndoableEditListener(this);
		resetUndoManager();
		revalidate();

		lastOpenedFile = null;
		lastOpenedFileModified = 0;
		if (isAutoTitleWindow()) {
			setWindowTitle("Notepad");
		}
	}

	/**
	 * Opens the specified file.
	 * 
	 * @param file
	 *          the file to be opened.
	 */
	public void openFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("File is null.");
		}

		if (file.isFile() && file.canRead()) {
			// Set the last opened file.
			this.lastOpenedFile = file;
			this.lastOpenedFileModified = file.lastModified();
			if (isAutoTitleWindow()) {
				setWindowTitle("Notepad - " + lastOpenedFile.getAbsolutePath());
			}

			// Interrupt the loader if it's currently loading a file.
			if ((loader != null) && loader.isAlive()) {
				loader.interrupt();
				try {
					loader.join();
				} catch (InterruptedException e) {
				}
			}

			// Remove the undoable listener from the document.
			Document old = getEditor().getDocument();
			if (old != null) {
				old.removeUndoableEditListener(this);
			}
			// Set a new document.
			getEditor().setDocument(createDocument());

			// Remove all highlights.
			removeHighlights();

			// Start a new loader.
			loader = new FileLoader(file, getEditor().getDocument());
			loader.start();
		} else {
			showErrorMessage("No se ha podido abrir el archivo: " + file);
		}
	}

	/**
	 * Saves the currently edited file. If the file is a new file, the user will be prompted to choose a destination.
	 */
	public void saveFile() {
		if (lastOpenedFile == null) {
			saveFileAs();
			if (isAutoTitleWindow()) {
				setWindowTitle("Notepad" + ((lastSavedFile == null) ? "" : " - " + lastSavedFile.getAbsolutePath()));
			}
		} else {
			new FileSaver(lastOpenedFile, editor.getDocument()).start();
		}
	}

	/**
	 * Saves the currently edited file and prompts the user to choose a destination.
	 */
	public void saveFileAs() {
		JFileChooser chooser = new AskOverwriteFileChooser(lastSavedFile);
		if (chooser.showSaveDialog(getWindow()) == JFileChooser.APPROVE_OPTION) {
			lastSavedFile = chooser.getSelectedFile();
			if (lastOpenedFile == null) {
				// We're saving a new file.
				lastOpenedFile = lastSavedFile;
			}
			new FileSaver(lastSavedFile, editor.getDocument()).start();
		}
	}

	/**
	 * Goes to the specified line. The user is prompted to type a line number.
	 */
	public void gotoLine() {
		Document doc = getEditor().getDocument();
		boolean error = true;
		int line = 0;
		int lineCount = doc.getDefaultRootElement().getElementCount();

		while (error) {
			// Reset the line.
			line = 0;
			String result = JOptionPane.showInputDialog(Resources.getMessage(Resources.PATTERN_TYPE_LINE_NUMBER, String.valueOf(lineCount)));
			// User cancelled the operation.
			if (result == null) {
				return;
			}
			try {
				line = Integer.parseInt(result);
				if ((line >= 1) && (line <= lineCount)) {
					error = false;
				}
			} catch (NumberFormatException e) {
			}

			if (error) {
				showErrorMessage(Resources.getString(Resources.MESSAGE_INVALID_LINE_NUMBER));
			}
		}

		// Line is 1-based.
		try {
			getEditor().setCaretPosition(doc.getDefaultRootElement().getElement(line - 1).getStartOffset());
		} catch (Exception e) {
		}
	}

	public void addHighlights(Pattern pattern, Color color) {
		// Get the highlighter.
		Highlighter highlighter = editor.getHighlighter();
		try {
			Document document = editor.getDocument();
			String text = document.getText(0, document.getLength());
			Matcher matcher = pattern.matcher(text);
			DefaultHighlightPainter painter = new DefaultHighlightPainter(color);
			while (matcher.find()) {
				highlights.add(highlighter.addHighlight(matcher.start(), matcher.end(), painter));
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void removeHighlights() {
		Highlighter hilite = editor.getHighlighter();
		// hilite.removeAllHighlights();
		// Highlighter.Highlight[] hilites = hilite.getHighlights();

		for (int i = 0; i < highlights.size(); i++) {
			hilite.removeHighlight(highlights.get(i));
		}
		highlights.clear();
		// for (int i = 0; i < hilites.length; i++) {
		// if (hilites[i].getPainter() instanceof MyHighlightPainter) {
		// hilite.removeHighlight(hilites[i]);
		// }
		// }
	}

	/**
	 * Shows an error message.
	 * 
	 * @param message
	 *          the message to show.
	 */
	private void showErrorMessage(String message) {
		SwingUtils.showErrorDialog(getWindow(), message);
	}

	/**
	 * Sets a title depending on panel's window ancestor (either a dialog or a frame).
	 * 
	 * @param title
	 *          the title to set.
	 */
	private void setWindowTitle(String title) {
		Window window = getWindow();
		if (window != null) {
			if (window instanceof Dialog) {
				((Dialog) window).setTitle(title);
			} else if (window instanceof Frame) {
				((Frame) window).setTitle(title);
			}
		}
	}

	public void showDialog(Frame frame) {
		if (dialog == null) {
			dialog = new JDialog(frame, false);
			dialog.getContentPane().add(createMenubar(), BorderLayout.NORTH);
			dialog.getContentPane().add(this, BorderLayout.CENTER);
			dialog.pack();
			dialog.setLocationRelativeTo(frame);
			if (isAutoTitleWindow()) {
				setWindowTitle("Notepad" + ((lastOpenedFile == null) ? "" : " - " + lastOpenedFile.getAbsolutePath()));
			}
		}
		dialog.setVisible(true);
	}

	/*
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
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
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/*
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/*
	 * @see javax.swing.event.UndoableEditListener#undoableEditHappened(javax.swing.event.UndoableEditEvent)
	 */
	public void undoableEditHappened(UndoableEditEvent e) {
		undo.addEdit(e.getEdit());
		undoAction.setEnabled(undo.canUndo());
		redoAction.setEnabled(undo.canRedo());
	}

	/*
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		if ((lastOpenedFile != null) && (lastOpenedFile.lastModified() != lastOpenedFileModified)) {
			if (ignoreNextFocusEvent) {
				ignoreNextFocusEvent = false;
			} else {
				if (JOptionPane.showConfirmDialog(getWindow(), "El archivo ha sido modificado desde su última apertura. ¿Desea actualizarlo?") == JOptionPane.YES_OPTION) {
					openFile(lastOpenedFile);
				} else {
					// User said 'No' or cancelled, so the editor will regain the focus. We must prevent the option pane to keep being showed again
					// and again, because a new focus gained event will be generated. I don't like this mechanism too much, but it works, however...
					ignoreNextFocusEvent = true;
				}
			}
		}
	}

	/*
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
	}

	/**
	 * Undo action.
	 */
	private class UndoAction extends AbstractAction {

		public UndoAction() {
			super(Resources.getString(Resources.ACTION_UNDO));
			putValue(Action.ACTION_COMMAND_KEY, "undo");
			putValue(Action.SMALL_ICON, Resources.getIcon("undo_16.png"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
			putValue(Action.SHORT_DESCRIPTION, Resources.getString(Resources.ACTION_UNDO_DESCRIPTION));
			setEnabled(false);
		}

		/*
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			try {
				undo.undo();
			} catch (CannotUndoException ex) {
			}
			update();
			redoAction.update();
		}

		protected void update() {
			setEnabled(undo.canUndo());
		}

	}

	/**
	 * Redo action.
	 */
	private class RedoAction extends AbstractAction {

		public RedoAction() {
			super(Resources.getString(Resources.ACTION_REDO));
			putValue(Action.ACTION_COMMAND_KEY, "redo");
			putValue(Action.SMALL_ICON, Resources.getIcon("redo_16.png"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
			putValue(Action.SHORT_DESCRIPTION, Resources.getString(Resources.ACTION_REDO_DESCRIPTION));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undo.redo();
			} catch (CannotRedoException ex) {
			}
			update();
			undoAction.update();
		}

		protected void update() {
			setEnabled(undo.canRedo());
		}

	}

	/**
	 * New file action.
	 */
	private class NewFileAction extends AbstractAction {

		public NewFileAction() {
			super(Resources.getString(Resources.ACTION_NEW_FILE));
			putValue(Action.ACTION_COMMAND_KEY, "new");
			putValue(Action.SMALL_ICON, Resources.getIcon("document_new_16.png"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
			putValue(Action.SHORT_DESCRIPTION, Resources.getString(Resources.ACTION_NEW_FILE_DESCRIPTION));
		}

		/*
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			newFile();
		}
	}

	/**
	 * Open action.
	 */
	private class OpenAction extends AbstractAction {

		public OpenAction() {
			super(Resources.getString(Resources.ACTION_OPEN_FILE));
			putValue(Action.ACTION_COMMAND_KEY, "open");
			putValue(Action.SMALL_ICON, Resources.getIcon("folder_16.png"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
			putValue(Action.SHORT_DESCRIPTION, Resources.getString(Resources.ACTION_OPEN_FILE_DESCRIPTION));
		}

		/*
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			Window window = getWindow();
			JFileChooser fc = new JFileChooser(lastOpenedFile);
			if (fc.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
				openFile(fc.getSelectedFile());
			}
		}
	}

	/**
	 * Save action.
	 */
	private class SaveAction extends AbstractAction {

		public SaveAction() {
			super(Resources.getString(Resources.ACTION_SAVE));
			putValue(Action.ACTION_COMMAND_KEY, "save");
			putValue(Action.SMALL_ICON, Resources.getIcon("disk_blue_16.png"));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
			putValue(Action.SHORT_DESCRIPTION, Resources.getString(Resources.ACTION_SAVE_DESCRIPTION));
		}

		/*
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			saveFile();
		}

	}

	/**
	 * 'Save as' action.
	 */
	private class SaveAsAction extends AbstractAction {

		public SaveAsAction() {
			super(Resources.getString(Resources.ACTION_SAVE_AS));
			putValue(Action.ACTION_COMMAND_KEY, "saveas");
			putValue(Action.SMALL_ICON, Resources.getIcon("save_as_16.png"));
			putValue(Action.SHORT_DESCRIPTION, Resources.getString(Resources.ACTION_SAVE_AS_DESCRIPTION));
		}

		/*
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}

	}

	/**
	 * 'Go to line' action.
	 */
	private class GotoLineAction extends AbstractAction {

		public GotoLineAction() {
			super(Resources.getString(Resources.ACTION_GO_TO_LINE));
			putValue(Action.ACTION_COMMAND_KEY, "goto");
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
			putValue(Action.SHORT_DESCRIPTION, Resources.getString(Resources.ACTION_GO_TO_LINE_DESCRIPTION));
		}

		/*
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			gotoLine();
		}

	}

	/**
	 * Thread to load a file into the text storage model (a document).
	 */
	private class FileLoader extends Thread {

		/**
		 * The file to load data from.
		 */
		private File file;

		/**
		 * The document to load data to.
		 */
		private Document document;

		public FileLoader(File file, Document document) {
			this.file = file;
			this.document = document;
			this.setPriority(Thread.MIN_PRIORITY);
		}

		/*
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			JProgressBar progress = new JProgressBar(0, (int) file.length());

			status.removeAll();
			status.add(progress);
			status.revalidate();

			try {
				// Try to start reading.
				UnicodeInputStream uis = new UnicodeInputStream(new FileInputStream(file));
				String encoding = uis.getEncoding();

				Reader reader = new BufferedReader(new InputStreamReader(uis, (encoding == null) ? uis.getDefaultEncoding() : encoding));
				char[] buff = new char[4096];
				int n;

				while ((n = reader.read(buff, 0, buff.length)) != -1 && !isInterrupted()) {
					document.insertString(document.getLength(), new String(buff, 0, n), null);
					progress.setValue(progress.getValue() + n);
				}
				reader.close();
				fireFileOpened();
			} catch (IOException e) {
				final String msg = e.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						showErrorMessage(Resources.getMessage(Resources.PATTERN_ERROR_COULDNT_OPEN_FILE, new String[] { file.getAbsolutePath(), msg }));
					}
				});
			} catch (BadLocationException e) {
			}
			document.addUndoableEditListener(JNotepad.this);
			resetUndoManager();

			// Dismiss the progress bar.
			status.removeAll();
			status.revalidate();
		}
	}

	/**
	 * Thread to save a document to file.
	 */
	private class FileSaver extends Thread {

		private File f;
		private Document document;

		public FileSaver(File f, Document document) {
			this.f = f;
			this.document = document;
		}

		/*
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			JProgressBar progress = new JProgressBar(0, document.getLength());

			status.removeAll();
			status.add(progress);
			status.revalidate();

			try {
				// Guess the encoding.
				String encoding = System.getProperty("file.encoding");
				if ((lastOpenedFile != null) && lastOpenedFile.exists()) {
					UnicodeInputStream uis = new UnicodeInputStream(new FileInputStream(f));
					encoding = uis.getEncoding();
					uis.close();
				}

				// System.out.println("Encoding write: " + encoding);

				// Try to start writing.
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), encoding));
				Segment text = new Segment();
				text.setPartialReturn(true);
				int charsLeft = document.getLength();
				int offset = 0;
				while (charsLeft > 0) {
					document.getText(offset, Math.min(4096, charsLeft), text);
					out.write(text.array, text.offset, text.count);
					charsLeft -= text.count;
					offset += text.count;
					progress.setValue(offset);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				out.flush();
				out.close();

				// Necessary to avoid the 'file updated'.
				if (f.equals(lastOpenedFile)) {
					lastOpenedFileModified = f.lastModified();
				}
			} catch (IOException e) {
				final String msg = e.getMessage();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						showErrorMessage(Resources.getMessage(Resources.PATTERN_ERROR_COULDNT_SAVE_FILE, msg));
					}
				});
			} catch (BadLocationException e) {
				System.err.println(e.getMessage());
			}
			// we are done... get rid of progressbar
			status.removeAll();
			status.revalidate();
		}
	}

	public static void main(String[] args) {
		JNotepad notepad = new JNotepad();

		JFrame f = new JFrame("Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(notepad);
		f.setJMenuBar(notepad.createMenubar());
		f.pack();
		f.setVisible(true);
	}

}