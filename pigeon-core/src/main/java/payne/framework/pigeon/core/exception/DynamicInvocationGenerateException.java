package payne.framework.pigeon.core.exception;

import java.lang.reflect.Method;

public class DynamicInvocationGenerateException extends Exception {
	private static final long serialVersionUID = -6850045782407467835L;

	private final Method method;

	public DynamicInvocationGenerateException(String message, Method method) {
		super(message);
		this.method = method;
	}

	public DynamicInvocationGenerateException(Throwable cause, Method method) {
		super(cause);
		this.method = method;
	}

	public DynamicInvocationGenerateException(String message, Throwable cause, Method method) {
		super(message, cause);
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

}
