package payne.framework.pigeon.server.exception;

public class ContextRunningException extends RuntimeException {
	private static final long serialVersionUID = -8798677580792891358L;

	public ContextRunningException() {
		super();
	}

	public ContextRunningException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContextRunningException(String message) {
		super(message);
	}

	public ContextRunningException(Throwable cause) {
		super(cause);
	}

}
