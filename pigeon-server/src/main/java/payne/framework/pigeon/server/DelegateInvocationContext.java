package payne.framework.pigeon.server;

public interface DelegateInvocationContext extends InvocationContext {

	void setDelegate(InvocationContext delegate);

}
