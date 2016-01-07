package payne.framework.pigeon.integration;

import payne.framework.pigeon.server.InvocationContext;

public interface DelegateInvocationContext extends InvocationContext {

	void setDelegate(InvocationContext delegate);

}
