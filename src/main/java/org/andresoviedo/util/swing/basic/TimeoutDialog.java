package org.andresoviedo.util.swing.basic;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


/**
 * A TimeoutDialog it's a normal Dialog, but implements a mechanism for returning a default response after a specified amount of time. If the
 * user sets a response before the specified amount of time, the dialog response it's the user response, otherwise the response it's the
 * default response. When the dialog is shown, a thread will be started and it will wait for the specified amount of time before setting the
 * dialog the default response.
 * 
 * @author aoviedo
 */
public class TimeoutDialog implements Runnable {
	String _msg = null;
	int _defaultValue;
	long _timeout = -1;

	JOptionPane _optionPane = null;
	JLabel _msgLabel = null;
	JDialog _dialog = null;

	Thread _thread = null;
	boolean _started = false;

	/**
	 * Creates and brings a modal dialog where the number of choices is determined by the <code>optionType</code> parameter, where the
	 * <code>messageType</code> parameter determines the icon to display. The <code>messageType</code> parameter is primarily used to supply a
	 * default icon from the Look and Feel.
	 * 
	 * @param parentComponent
	 *          determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>, or if the
	 *          <code>parentComponent</code> has no <code>Frame</code>, a default <code>Frame</code> is used.
	 * @param message
	 *          the <code>Object</code> to display
	 * @param title
	 *          the title string for the dialog
	 * @param optionType
	 *          an integer designating the options available on the dialog: <code>YES_NO_OPTION</code>, or <code>YES_NO_CANCEL_OPTION</code>
	 * @param messageType
	 *          an integer designating the kind of message this is; primarily used to determine the icon from the pluggable Look and Feel:
	 *          <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>, or
	 *          <code>PLAIN_MESSAGE</code>
	 * @return an integer indicating the option selected by the user
	 * @exception HeadlessException
	 *              if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static int showConfirmDialog(Component parentComponent, String message, String title, int optionType, int messageType,
			int defaultValue, long timeout) {
		TimeoutDialog dialog = new TimeoutDialog(parentComponent, message, title, optionType, messageType, defaultValue, timeout);
		dialog.show();
		return dialog.getValue();
	}

	/**
	 * Creates a modal dialog where the number of choices is determined by the <code>optionType</code> parameter, where the
	 * <code>messageType</code> parameter determines the icon to display. The <code>messageType</code> parameter is primarily used to supply a
	 * default icon from the Look and Feel.
	 * 
	 * @param parentComponent
	 *          determines the <code>Frame</code> in which the dialog is displayed; if <code>null</code>, or if the
	 *          <code>parentComponent</code> has no <code>Frame</code>, a default <code>Frame</code> is used.
	 * @param message
	 *          the <code>Object</code> to display
	 * @param title
	 *          the title string for the dialog
	 * @param optionType
	 *          an integer designating the options available on the dialog: <code>YES_NO_OPTION</code>, or <code>YES_NO_CANCEL_OPTION</code>
	 * @param messageType
	 *          an integer designating the kind of message this is; primarily used to determine the icon from the pluggable Look and Feel:
	 *          <code>ERROR_MESSAGE</code>, <code>INFORMATION_MESSAGE</code>, <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>, or
	 *          <code>PLAIN_MESSAGE</code>
	 * @return an integer indicating the option selected by the user
	 * @exception HeadlessException
	 *              if <code>GraphicsEnvironment.isHeadless</code> returns <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 * @see JOptionPane#showConfirmDialog(Component, Object, String, int, int)
	 */
	public TimeoutDialog(Component src, String msg, String title, int optionType, int style, int defaultValue, long timeout) {
		_msg = msg;
		_defaultValue = defaultValue;
		_timeout = timeout;

		_msgLabel = new JLabel(_msg);
		_optionPane = new JOptionPane(_msgLabel, style, optionType);
		_optionPane.setInitialValue(new Integer(_defaultValue));
		_optionPane.selectInitialValue();
		_dialog = _optionPane.createDialog(src, title);
		_dialog.setModal(true);
	}

	/**
	 * Shows the dialog and starts the thead wich will wait for a user response
	 */
	public void show() {
		// If timeout is set, the wait for a limited amount of time before setting a default response
		if (_timeout > 0) {
			_started = true;
			_thread = new Thread(this, "TimeoutDialog");
			_thread.start();
		}

		_dialog.setVisible(true);

		if (_thread != null) {
			_started = false;
			_thread.interrupt();
		}

		_dialog.dispose();
	}

	/**
	 * Returns the dialog response. It should be called after calling the {@link #show()} method.
	 * 
	 * @return The dialog response
	 */
	public int getValue() {
		int ret = JOptionPane.CLOSED_OPTION;
		Object selectedValue = _optionPane.getValue();
		if (selectedValue != null && selectedValue instanceof Integer) {
			ret = ((Integer) selectedValue).intValue();
		}
		return ret;
	}

	/**
	 * Thread routine for waiting a specified amount of time. It also updates the dialog message with information about the amount of time
	 * that lefts for dialog be closed
	 */
	public void run() {
		try {
			for (long i = _timeout; _started && i > 0; i -= 1000) {
				_msgLabel.setText("<html>" + _msg + "<br>Closing dialog in " + +(int) (i / 1000) + " sec...</html>");
				Thread.sleep(1000);
			}
			_optionPane.setValue(new Integer(_defaultValue));
		} catch (InterruptedException e) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}