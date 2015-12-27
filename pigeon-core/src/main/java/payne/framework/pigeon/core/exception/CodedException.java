package payne.framework.pigeon.core.exception;

import java.io.IOException;

/**
 * 带有编码的异常,通过注册的已编码异常处理器可以对异常进行处理
 * 
 * @author yangchangpei
 *
 */
public abstract class CodedException extends IOException {
	private static final long serialVersionUID = -1707531300719950292L;

	public CodedException() {
		super();
	}

	public CodedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodedException(String message) {
		super(message);
	}

	public CodedException(Throwable cause) {
		super(cause);
	}

	public abstract int getCode();

	public abstract String getReason();

}
