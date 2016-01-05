package payne.framework.pigeon.server.exception;

import payne.framework.pigeon.core.exception.CodedException;

public class UnrecognizedModeException extends CodedException {
	private static final long serialVersionUID = 555607021991423611L;
	private final String mode;

	public UnrecognizedModeException(String mode) {
		super("request method " + mode + " is unrecognized in this server");
		this.mode = mode;
	}

	@Override
	public int getCode() {
		return 400;
	}

	@Override
	public String getReason() {
		return "Bad Request";
	}

	public String getMode() {
		return mode;
	}

}
