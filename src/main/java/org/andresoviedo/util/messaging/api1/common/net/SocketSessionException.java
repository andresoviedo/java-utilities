package org.andresoviedo.util.messaging.api1.common.net;

/**
 * The exception thrown by socket session.
 * 
 * @author andres
 */
public class SocketSessionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1802099954036600745L;

	public SocketSessionException() {
		super();
	}

	public SocketSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SocketSessionException(String message) {
		super(message);
	}

	public SocketSessionException(Throwable cause) {
		super(cause);
	}

}
