package org.andresoviedo.util.messaging.api1.server;

/**
 * The exception used by server session.
 * 
 * @author andresoviedo
 */
public class ServerSessionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7704845828744529473L;

	public ServerSessionException() {
		super();
	}

	public ServerSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerSessionException(String message) {
		super(message);
	}

	public ServerSessionException(Throwable cause) {
		super(cause);
	}

}
