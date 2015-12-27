package payne.framework.pigeon.client.exception;

import payne.framework.pigeon.client.Invoker;

public class UnexecutedInvokeException extends RuntimeException {
	private static final long serialVersionUID = -6749836353955160227L;

	private final Invoker<?> invoker;

	public UnexecutedInvokeException(Invoker<?> invoker) {
		super("invocation has not been executed please call invoke() before this operation");
		this.invoker = invoker;
	}

	public Invoker<?> getInvoker() {
		return invoker;
	}

}
