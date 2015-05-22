package org.andresoviedo.util.messaging.api1;

/**
 * Exceptions thrown by the messenger class.
 * 
 * @author andres
 */
public class MessengerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1972627689708510772L;

	public MessengerException() {
	}

	public MessengerException(String message) {
		super(message);
	}

	public MessengerException(Throwable cause) {
		super(cause);
	}

	public MessengerException(String message, Throwable cause) {
		super(message, cause);
	}

}
