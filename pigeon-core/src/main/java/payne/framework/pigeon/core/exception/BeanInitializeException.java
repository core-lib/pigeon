package payne.framework.pigeon.core.exception;

/**
 * bean 初始化异常
 * 
 * @author ron
 * 
 */
public class BeanInitializeException extends RuntimeException {
	private static final long serialVersionUID = -428169839683273719L;

	private final Class<?> type;
	private final String name;

	public BeanInitializeException(Class<?> type, String name) {
		super();
		this.type = type;
		this.name = name;
	}

	public BeanInitializeException(Throwable cause, Class<?> type, String name) {
		super(cause);
		this.type = type;
		this.name = name;
	}

	public BeanInitializeException(String message, Throwable cause, Class<?> type, String name) {
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
