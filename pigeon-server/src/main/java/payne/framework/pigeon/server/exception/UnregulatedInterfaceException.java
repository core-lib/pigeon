package payne.framework.pigeon.server.exception;

import java.lang.reflect.Method;

public class UnregulatedInterfaceException extends Exception {
	private static final long serialVersionUID = -7229979524189255392L;

	protected final Class<?> interfase;
	protected final Method method;

	public UnregulatedInterfaceException(String message, Class<?> interfase, Method method) {
		super(message);
		this.interfase = interfase;
		this.method = method;
	}

	public UnregulatedInterfaceException(Throwable cause, Class<?> interfase, Method method) {
		super(cause);
		this.interfase = interfase;
		this.method = method;
	}

	public UnregulatedInterfaceException(String message, Throwable cause, Class<?> interfase, Method method) {
		super(message, cause);
		this.interfase = interfase;
		this.method = method;
	}

	public Class<?> getInterfase() {
		return interfase;
	}

	public Method getMethod() {
		return method;
	}

}
