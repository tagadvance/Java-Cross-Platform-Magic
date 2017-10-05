package com.tagadvance.platform;

public class PlatformDependencyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlatformDependencyException() {
		super();
	}

	public PlatformDependencyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PlatformDependencyException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlatformDependencyException(String message) {
		super(message);
	}

	public PlatformDependencyException(Throwable cause) {
		super(cause);
	}

}
