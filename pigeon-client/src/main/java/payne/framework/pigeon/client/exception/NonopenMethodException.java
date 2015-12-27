package payne.framework.pigeon.client.exception;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 调用关闭的方法异常
 * 
 * @author ron
 * 
 */
public class NonopenMethodException extends IOException {
	private static final long serialVersionUID = 6686624746204114511L;

	private final Class<?> interfase;
	private final Method method;
	private final Object[] arguments;

	public NonopenMethodException(Class<?> interfase, Method method, Object[] arguments) {
		super("can not invoke a closed method " + method.getName() + " of " + interfase + " which server would not handle");
		this.interfase = interfase;
		this.method = method;
		this.arguments = arguments;
	}

	public Class<?> getInterfase() {
		return interfase;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArguments() {
		return arguments;
	}

}
