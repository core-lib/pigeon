package payne.framework.pigeon.core.dynamic;

import java.util.Set;

import payne.framework.pigeon.core.exception.DynamicInvocationArchiveException;

public interface DynamicInvocationArchiver {

	void archive(String name, Set<Generation> generations) throws DynamicInvocationArchiveException;

}
