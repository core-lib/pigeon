package payne.framework.pigeon.server.exception;

public class IllegalOperationException extends RuntimeException {
	private static final long serialVersionUID = 4434402545318736103L;

	public IllegalOperationException() {
		super();
	}

	public IllegalOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalOperationException(String message) {
		super(message);
	}

	public IllegalOperationException(Throwable cause) {
		super(cause);
	}

}
