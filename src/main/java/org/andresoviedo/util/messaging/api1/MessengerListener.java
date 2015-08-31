package org.andresoviedo.util.messaging.api1;

import java.util.EventListener;

import org.andresoviedo.util.messaging.api1.common.net.SocketSession;

/**
 * The interface an object interested in messenger events has to implement.
 * 
 * @author andresoviedo
 * @since 2.0.10
 */
public interface MessengerListener extends EventListener {

	/**
	 * Invoked when a socket session has been opened and authenticated.
	 * 
	 * @param session
	 *            the session that has been opened.
	 */
	public void sessionOpened(SocketSession session);

	/**
	 * Invoked when an authenticated socket session has been closed.
	 * 
	 * @param session
	 *            the session that has been closed.
	 * @param forced
	 *            <code>true</code> if the session has been forced to be closed using its <code>close()</code> method.
	 */
	public void sessionClosed(SocketSession session, boolean forced);

}
