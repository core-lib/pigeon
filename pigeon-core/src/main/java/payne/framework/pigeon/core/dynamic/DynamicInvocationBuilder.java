package payne.framework.pigeon.core.dynamic;

import payne.framework.pigeon.core.exception.DynamicInvocationBuildException;

public interface DynamicInvocationBuilder {

	void build(String implementation, Class<?> interfase) throws DynamicInvocationBuildException;

}
