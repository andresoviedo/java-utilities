package org.andresoviedo.util.messaging.api1.common.io;

/**
 * The exception used by the persistence mechanism.
 * 
 * @author andresoviedo
 */
public class PersistenceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2232108598844047485L;

	public PersistenceException() {
		super();
	}

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(Throwable cause) {
		super(cause);
	}

}
