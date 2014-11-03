package org.andresoviedo.util.swing.jnotepad;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.andresoviedo.util.swing.SwingUtils;
import org.andresoviedo.util.swing.jnotepad.resources.Resources;
import org.andresoviedo.util.swing.jnotepad.utils.NotepadUtils;


public class NotepadGlyphGutter extends JComponent implements DocumentListener, PropertyChangeListener, ItemListener, MouseListener {

	/**
	 * The line number should be right justified.
	 */
	public static int JUSTIFY_LEFT = 0;

	/**
	 * The line number should be left justified.
	 */
	public static int JUSTIFY_RIGHT = 1;

	/**
	 * The associated text component.
	 */
	private JTextComponent component;

	/**
	 * A popup menu to set glyph gutter's options.
	 */
	private JPopupMenu popup;

	/**
	 * 'Show line numbers' menu item.
	 */
	private JMenuItem miShowLineNumbers;

	/**
	 * Gutter's Left gap.
	 */
	private int leftGap = 10;

	/**
	 * Gutter's right gap.
	 */
	private int rightGap = 5;

	/**
	 * Stores the line ascent for optimize the painting process.
	 */
	private int lineAscent;

	/**
	 * Stores the line height for optimize the painting process.
	 */
	private int lineHeight;

	/**
	 * Stores the width of the wider number (from 0 to 9) for optimize the painting process.
	 */
	private int lineNumberDigitWidth;

	/**
	 * Keeps track of the current number of lines in the document.
	 */
	private int currentLineCount;

	/**
	 * The line number justification.
	 */
	private int justification = JUSTIFY_RIGHT;

	/**
	 * Indicates whether to show the line numbers or not.
	 */
	private boolean showLineNumbers = true;

	/**
	 * Creates a new notepad glyph gutter.
	 * 
	 * @param notepad
	 *          the associated notepad.
	 */
	public NotepadGlyphGutter(JNotepad notepad) {
		this.component = notepad.getEditor();
		this.component.addPropertyChangeListener(this);
		this.initialize();
	}

	/**
	 * Initializes the glyph gutter.
	 */
	private void initialize() {
		setFont(component.getFont());
		setBackground(UIManager.getColor("textHighlight"));
		setForeground(UIManager.getColor("textHighlightText"));

		createPopup();

		this.lineAscent = getLineAscent();
		this.lineHeight = getLineHeight();
		this.lineNumberDigitWidth = getLineNumberDigitWidth();

		this.documentChanged(null, component.getDocument());
		this.addMouseListener(this);
	}

	/**
	 * Creates the popup menu.
	 */
	private void createPopup() {
		popup = new JPopupMenu();

		miShowLineNumbers = popup.add(new JCheckBoxMenuItem(Resources.getString(Resources.ACTION_SHOW_LINE_NUMBERS), true));
		miShowLineNumbers.addItemListener(this);
	}

	/**
	 * Returns whether line numbers are shown or not.
	 * 
	 * @return <code>true</code> if line numbers are shown, <code>false</code> otherwise.
	 */
	public boolean isShowLineNumbers() {
		return showLineNumbers;
	}

	/**
	 * Set whether line numbers have to be shown or not.
	 * 
	 * @param showLineNumbers
	 *          <code>true</code> if line numbers have to be shown, <code>false</code> otherwise.
	 */
	public void setShowLineNumbers(boolean showLineNumbers) {
		if (this.showLineNumbers != showLineNumbers) {
			this.showLineNumbers = showLineNumbers;
			this.resizeAndRepaint();
		}
	}

	/**
	 * Returns the width needed to draw the highest line number.
	 * 
	 * @return the width needed to draw the highest line number.
	 */
	private int getLineNumberWidth() {
		return NotepadUtils.getDigitCount(NotepadUtils.getLineCount(component.getDocument())) * lineNumberDigitWidth;
	}

	/**
	 * Returns the width of the wider digit character (from 0 to 9).
	 * 
	 * @return the width of the wider digit character (from 0 to 9).
	 */
	private int getLineNumberDigitWidth() {
		FontMetrics fm = SwingUtils.getFontMetrics(getFont(), this);
		int result = 1;
		for (int i = 0; i <= 9; i++) {
			result = Math.max(result, fm.charWidth((char) ('0' + i)));
		}
		return result;
	}

	/**
	 * Returns the line ascent.
	 * 
	 * @return the line ascent.
	 */
	private int getLineAscent() {
		FontMetrics fm = SwingUtils.getFontMetrics(getFont(), this);
		return fm.getAscent();
	}

	/**
	 * Returns the line height.
	 * 
	 * @return the line height.
	 */
	private int getLineHeight() {
		FontMetrics fm = SwingUtils.getFontMetrics(getFont(), this);
		return fm.getHeight();
	}

	/**
	 * Resizes the gutter based on document information.
	 */
	private void resize() {
		Dimension dim = new Dimension();
		dim.width = leftGap + rightGap + ((showLineNumbers) ? getLineNumberWidth() : 0);
		dim.height = component.getHeight() + this.currentLineCount * this.lineHeight;

		setPreferredSize(dim);
		revalidate();
	}

	/*
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		// Default painting.
		super.paintComponent(g);

		Rectangle clip = g.getClipBounds();

		// Paint the background.
		g.setColor(getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		// Draw a thin line to show a separation between the gutter and the component.
		g.setColor(getForeground());
		g.drawLine(getWidth() - 1, clip.y, getWidth() - 1, clip.y + clip.height);

		if (!showLineNumbers) {
			return;
		}

		// Get a reference to the document.
		AbstractDocument doc = (AbstractDocument) component.getDocument();

		// Compute the start and end offsets.
		int startOffset = component.viewToModel(new Point(0, clip.y));
		int endOffset = component.viewToModel(new Point(0, clip.y + clip.height));

		// Get the start and end lines.
		int startLine = doc.getDefaultRootElement().getElementIndex(startOffset);
		int endLine = doc.getDefaultRootElement().getElementIndex(endOffset);

		doc.readLock();
		try {
			// int y = 0;
			Rectangle r = null;
			for (int i = startLine; i <= endLine; i++) {
				r = component.modelToView(doc.getDefaultRootElement().getElement(i).getStartOffset());
				paintLineNumber(g, i, r);
			}
		} catch (BadLocationException e) {
		} finally {
			doc.readUnlock();
		}
	}

	/**
	 * Paints the specified line number.
	 * 
	 * @param g
	 *          the graphics object.
	 * @param line
	 *          the line number to paint (0-based).
	 * @param viewRect
	 *          the rectangle of the view of the corresponding line.
	 */
	private void paintLineNumber(Graphics g, int line, Rectangle viewRect) {
		String s = String.valueOf(line + 1);
		FontMetrics fm = SwingUtils.getFontMetrics(g.getFont(), g);

		int x = (justification == JUSTIFY_LEFT) ? leftGap : getSize().width - fm.stringWidth(s) - rightGap;
		int y = viewRect.y + (viewRect.height + lineAscent) / 2;

		g.drawString(s, x, y);
	}

	/**
	 * Invoked when a new document has been set in the editor component.
	 * 
	 * @param oldDoc
	 *          the old document.
	 * @param newDoc
	 *          the new document.
	 */
	private void documentChanged(Document oldDoc, Document newDoc) {
		if (oldDoc != null) {
			oldDoc.removeDocumentListener(this);
		}
		if (newDoc != null) {
			currentLineCount = NotepadUtils.getLineCount(newDoc);
			newDoc.addDocumentListener(this);
		}
		resizeAndRepaint();
	}

	/**
	 * Invoked when the current document has changed.
	 * 
	 * @param doc
	 *          the document.
	 */
	private void documentChanged(Document doc) {
		int lineCount = NotepadUtils.getLineCount(doc);
		if (currentLineCount != lineCount) {
			currentLineCount = lineCount;
			resizeAndRepaint();
		}
	}

	/**
	 * Resizes and repaints the component.
	 */
	private void resizeAndRepaint() {
		if (SwingUtilities.isEventDispatchThread()) {
			resize();
			repaint();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					resize();
					repaint();
				}
			});
		}
	}

	/*
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if ("document".equals(e.getPropertyName())) {
			Document oldDoc = (Document) e.getOldValue();
			Document newDoc = (Document) e.getNewValue();
			documentChanged(oldDoc, newDoc);
		}
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
		documentChanged(e.getDocument());
	}

	/*
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e) {
		documentChanged(e.getDocument());
	}

	/*
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		setShowLineNumbers(e.getStateChange() == ItemEvent.SELECTED);
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

}