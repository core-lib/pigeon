package payne.framework.pigeon.server.exception;

import java.lang.reflect.Method;

/**
 * 重复注册开放接口异常
 * 
 * @author yangchangpei
 * 
 */
public class DuplicatePathException extends UnregulatedInterfaceException {
	private static final long serialVersionUID = -2938247400435850485L;

	private final String path;

	public DuplicatePathException(String path, Class<?> interfase, Method method) {
		super("duplicate path " + path + " defined in method " + method.getName() + " of " + interfase, interfase, method);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
