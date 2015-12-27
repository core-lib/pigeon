package payne.framework.pigeon.server.exception;

import payne.framework.pigeon.core.exception.CodedException;

public class UnrecognizedHeadException extends CodedException {
	private static final long serialVersionUID = 5708542067305675228L;

	public UnrecognizedHeadException() {
		super();
	}

	public UnrecognizedHeadException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnrecognizedHeadException(String message) {
		super(message);
	}

	public UnrecognizedHeadException(Throwable cause) {
		super(cause);
	}

	@Override
	public int getCode() {
		return 406;
	}

	@Override
	public String getReason() {
		return "Not Acceptable";
	}

}
