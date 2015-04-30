package org.andresoviedo.util.swing.jnotepad;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class FindReplaceBasicCommandExecutor implements FindReplaceCommandExecutor, CaretListener {

	// private Object tag;

	private JTextComponent textComponent;

	private FindReplaceOptions options;

	public FindReplaceBasicCommandExecutor(JTextComponent textComponent, FindReplaceOptions options) {
		this.textComponent = textComponent;
		this.textComponent.addCaretListener(this);
		this.options = options;
	}

	/*
	 */
	public boolean find() {
		// Indicates if the search has to be performed forward.
		boolean forward = (options.getDirection() == FindReplaceOptions.DIRECTION_FORWARD);

		// The position from which to get the text.
		int pos = (forward) ? textComponent.getSelectionEnd() : textComponent.getSelectionStart();

		// Get the document.
		Document document = textComponent.getDocument();

		// Get the text depending on the direction.
		String text = null;
		try {
			// if (options.getScope() == FindTextOptions.SCOPE_ALL) {
			if (forward) {
				text = document.getText(pos, document.getLength() - pos);
			} else {
				text = document.getText(0, pos);
			}
			// } else {
			// text = textComponent.getSelectedText();
			// }
		} catch (Exception e) {
			return false;
		}

		// Get the pattern.
		Pattern pattern = getPattern();
		if (pattern == null) {
			return false;
		}

		Matcher matcher = pattern.matcher(text);

		int start = -1;
		int end = -1;

		while (matcher.find()) {
			start = matcher.start();
			end = matcher.end();
			if (forward) {
				break;
			}
		}

		if (start != -1) {
			textComponent.setSelectionStart(forward ? pos + start : start);
			textComponent.setSelectionEnd(textComponent.getSelectionStart() + end - start);
			return true;
		} else {
			return false;
		}
	}

	/*
	 */
	public void replace() {
		int start = textComponent.getSelectionStart();
		int end = textComponent.getSelectionEnd();
		if (start < end) {
			Document document = textComponent.getDocument();
			try {
				document.remove(start, end - start);
				document.insertString(start, options.getReplaceText(), null);
			} catch (BadLocationException e) {
			}
		}
	}

	/*
	 */
	public int replaceAll() {
		int result = 0;
		int start;
		int end;

		Document document = textComponent.getDocument();

		Pattern pattern = getPattern();
		Matcher matcher = pattern.matcher(textComponent.getText());
		while (matcher.find()) {
			start = matcher.start();
			end = matcher.end();
			try {
				document.remove(start, end - start);
				document.insertString(start, options.getReplaceText(), null);
				matcher.reset(document.getText(0, document.getLength()));
			} catch (BadLocationException e) {
				break;
			}
			result++;
		}

		return result;
	}

	/*
	 */
	public boolean replaceFind() {
		replace();
		return find();
	}

	/*
	 */
	// public void scopeChanged() {
	// if (options.getScope() == FindTextOptions.SCOPE_SELECTED_LINES) {
	// int[] lines = getLinesToSelect();
	// if (lines[0] != lines[1]) {
	// int start = textComponent.getSelectionStart();
	// int end = textComponent.getSelectionEnd();
	// try {
	// tag = textComponent.getHighlighter().addHighlight(start, end + 10, new MyHighlightPainter(Color.lightGray));
	// } catch (BadLocationException e) {
	// e.printStackTrace();
	// }
	// }
	// textComponent.getCaret().setDot(textComponent.getCaret().getDot());
	// } else if (tag != null) {
	// textComponent.getHighlighter().removeHighlight(tag);
	// tag = null;
	// }
	// }
	private Pattern getPattern() {
		StringBuffer sb = new StringBuffer(options.getText());
		if (!options.isUseRegex()) {
			// Escape all characters.
			sb.insert(0, "\\Q");
			sb.append("\\E");
			// Match whole word makes sense when not using regexps.
			if (options.isMatchWholeWord()) {
				sb.insert(0, "\\b");
				sb.append("\\b");
			}
			// sb.insert(0, ".*");
			// sb.append(".*");
		}

		try {
			return Pattern.compile(sb.toString(), options.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		} catch (PatternSyntaxException e) {
		}
		return null;
	}

	// private int[] getLinesToSelect() {
	// Element element = textComponent.getDocument().getDefaultRootElement();
	// int index1 = element.getElementIndex(textComponent.getSelectionStart());
	// int index2 = element.getElementIndex(textComponent.getSelectionEnd());
	//
	// int start = element.getElement(index1).getStartOffset();
	// int end = element.getElement(index2).getEndOffset();
	//
	// return new int[] { start, end };
	// }

	/*
	 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate(CaretEvent e) {
		if (e.getDot() == e.getMark()) {
			options.disableReplace();
			// options.setScope(FindTextOptions.SCOPE_ALL);
			// Remove the highlight.
			// if (tag != null) {
			// textComponent.getHighlighter().removeHighlight(tag);
			// tag = null;
			// }
		}
	}

	/**
	 * A private subclass of the default highlight painter.
	 */
	// private class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
	// public MyHighlightPainter(Color color) {
	// super(color);
	// }
	// }
}