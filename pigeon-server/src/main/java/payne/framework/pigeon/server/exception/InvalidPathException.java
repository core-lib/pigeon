package payne.framework.pigeon.server.exception;

import java.lang.reflect.Method;

/**
 * 不正确的路径定义异常
 * 
 * @author ron
 * 
 */
public class InvalidPathException extends UnregulatedInterfaceException {
	private static final long serialVersionUID = -3225825891551797395L;

	private final String path;

	public InvalidPathException(String path, Class<?> interfase) {
		super(path + " is an invalidate path definition in " + interfase, interfase, null);
		this.path = path;
	}

	public InvalidPathException(String path, Class<?> interfase, Method method) {
		super(path + " is an invalidate path definition in method " + method.getName() + " of " + interfase, interfase, method);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
