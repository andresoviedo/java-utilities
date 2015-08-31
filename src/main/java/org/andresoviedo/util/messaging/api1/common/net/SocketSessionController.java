package org.andresoviedo.util.messaging.api1.common.net;

import org.andresoviedo.util.messaging.api1.common.data.Command;

/**
 * The interface an object interested in socket session events has to implement.
 * 
 * @author andresoviedo
 */
public interface SocketSessionController {

	/**
	 * Invoked when the session has been opened.
	 * 
	 * @param session
	 *            the socket session that has been opened.
	 */
	public void sessionOpened(SocketSession session);

	/**
	 * Invoked when the session has been closed.
	 * 
	 * @param session
	 *            the session that has been closed.
	 * @param forced
	 *            <code>true</code> if the session has been forced to be closed using its <code>close()</code> method.
	 */
	public void sessionClosed(SocketSession session, boolean forced);

	/**
	 * Invoked when a command has been sent.
	 * 
	 * @param session
	 *            the session through which the message has been sent.
	 * @param command
	 *            the command that has been sent.
	 */
	public void commandSent(SocketSession session, Command command);

	/**
	 * Invoked when a command has been received.
	 * 
	 * @param session
	 *            the session through which the message has been received.
	 * @param command
	 *            the command that has been received.
	 */
	public void commandReceived(SocketSession session, Command command);

}