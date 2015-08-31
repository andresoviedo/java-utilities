package org.andresoviedo.util.data;

/**
 * Exception thrown when a queue is empty and someone's trying to get or peek an object from it.
 * 
 */
public class EmptyQueueException extends RuntimeException {

	public EmptyQueueException() {
		super();
	}

	public EmptyQueueException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyQueueException(String message) {
		super(message);
	}

	public EmptyQueueException(Throwable cause) {
		super(cause);
	}

}