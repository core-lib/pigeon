package payne.framework.pigeon.core.exception;

import java.io.IOException;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.formatting.DataFormatter;

/**
 * 数据格式化异常
 * 
 * @author yangchangpei
 *
 */
public class FormatterException extends IOException {
	private static final long serialVersionUID = 5926265800221315037L;
	/**
	 * 格式化器
	 */
	private final DataFormatter<?> formatter;
	/**
	 * 数据
	 */
	private final Object Data;
	/**
	 * 数据类型对应类型
	 */
	private final Method method;

	public FormatterException(Throwable cause, DataFormatter<?> formatter, Object data, Method method) {
		super(cause);
		this.formatter = formatter;
		Data = data;
		this.method = method;
	}

	public DataFormatter<?> getFormatter() {
		return formatter;
	}

	public Object getData() {
		return Data;
	}

	public Method getMethod() {
		return method;
	}

}
