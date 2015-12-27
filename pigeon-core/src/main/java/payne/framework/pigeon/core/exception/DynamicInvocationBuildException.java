package payne.framework.pigeon.core.exception;


public class DynamicInvocationBuildException extends Exception {
	private static final long serialVersionUID = -1792944770787754976L;

	private final Class<?> interfase;

	public DynamicInvocationBuildException(String message, Class<?> interfase) {
		super(message);
		this.interfase = interfase;
	}

	public DynamicInvocationBuildException(Throwable cause, Class<?> interfase) {
		super(cause);
		this.interfase = interfase;
	}

	public DynamicInvocationBuildException(String message, Throwable cause, Class<?> interfase) {
		super(message, cause);
		this.interfase = interfase;
	}

	public Class<?> getInterfase() {
		return interfase;
	}

}
