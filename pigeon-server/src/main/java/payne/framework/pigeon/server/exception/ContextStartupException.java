package payne.framework.pigeon.server.exception;

/**
 * 开放接口容器启动异常
 * 
 * @author ron
 * 
 */
public class ContextStartupException extends Exception {
	private static final long serialVersionUID = -2175414420545597349L;

	public ContextStartupException() {
		super();
	}

	public ContextStartupException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContextStartupException(String message) {
		super(message);
	}

	public ContextStartupException(Throwable cause) {
		super(cause);
	}

}
