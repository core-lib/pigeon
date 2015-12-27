package payne.framework.pigeon.client.exception;

import payne.framework.pigeon.client.Invoker;

public class UncompletedInvokeException extends RuntimeException {
	private static final long serialVersionUID = 6501552042074165032L;

	private final Invoker<?> invoker;

	public UncompletedInvokeException(Invoker<?> invoker) {
		super("can not reinvoke an invocation while it is uncompleted if you want to give up previous invocation please try to cancel it");
		this.invoker = invoker;
	}

	public Invoker<?> getInvoker() {
		return invoker;
	}

}
