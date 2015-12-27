package payne.framework.pigeon.core.exception;

import payne.framework.pigeon.core.Invocation;

/**
 * 重复调用一个已完成的invocation异常,即一个invocation已经处理完成则不应该再次发起调用
 * 
 * @author ron
 * 
 */
public class IllogicalInvokeException extends Exception {
	private static final long serialVersionUID = 1589266435591798390L;

	private final Invocation invocation;

	public IllogicalInvokeException(Invocation invocation) {
		super("your attempt to reinvoke a completed invocation,it is not permitted");
		this.invocation = invocation;
	}

	public Invocation getInvocation() {
		return invocation;
	}

}
