package com.tagadvance.platform;

public class UnsupportedPlatformException extends PlatformDependencyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedPlatformException() {
		super();
	}

	public UnsupportedPlatformException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnsupportedPlatformException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedPlatformException(String message) {
		super(message);
	}

	public UnsupportedPlatformException(Throwable cause) {
		super(cause);
	}

}
