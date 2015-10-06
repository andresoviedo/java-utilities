package org.andresoviedo.util.pool;

public class TimeoutException extends RuntimeException {

	private static final long serialVersionUID = 2012723978712110200L;

	public TimeoutException() {
		super();
	}

	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeoutException(String message) {
		super(message);
	}

	public TimeoutException(Throwable cause) {
		super(cause);
	}

}
