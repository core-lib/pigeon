package payne.framework.pigeon.core.exception;


public class ForbiddenInvocationException extends CodedException {
	private static final long serialVersionUID = 5507961724638771846L;

	public ForbiddenInvocationException(String message) {
		super(message);
	}

	@Override
	public int getCode() {
		return 403;
	}

	public String getReason() {
		return "Forbidden";
	}

}
