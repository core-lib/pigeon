package payne.framework.pigeon.core.exception;

import java.io.IOException;

import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.formatting.Structure;

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
	private final InvocationFormatter formatter;
	/**
	 * 数据
	 */
	private final Object Data;
	/**
	 * 数据类型对应类型
	 */
	private final Structure structure;

	public FormatterException(Throwable cause, InvocationFormatter formatter, Object data) {
		this(cause, formatter, data, null);
	}

	public FormatterException(Throwable cause, InvocationFormatter formatter, Object data, Structure structure) {
		super(cause);
		this.formatter = formatter;
		Data = data;
		this.structure = structure;
	}

	public InvocationFormatter getFormatter() {
		return formatter;
	}

	public Object getData() {
		return Data;
	}

	public Structure getStructure() {
		return structure;
	}

}
