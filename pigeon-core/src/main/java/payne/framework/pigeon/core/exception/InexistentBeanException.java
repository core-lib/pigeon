package payne.framework.pigeon.core.exception;

/**
 * 找不到bean异常
 * 
 * @author ron
 * 
 */
public class InexistentBeanException extends RuntimeException {
	private static final long serialVersionUID = -2304665755843533641L;
	private final Class<?> type;
	private final String name;

	public InexistentBeanException(Class<?> type, String name) {
		super("can not find bean named '" + name + "' of type " + type);
		this.type = type;
		this.name = name;
	}

	public InexistentBeanException(String message, Class<?> type, String name) {
		super(message);
		this.type = type;
		this.name = name;
	}

	public InexistentBeanException(Throwable cause, Class<?> type, String name) {
		super(cause);
		this.type = type;
		this.name = name;
	}

	public InexistentBeanException(String message, Throwable cause, Class<?> type, String name) {
		super(message, cause);
		this.type = type;
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
