package payne.framework.pigeon.core;

import payne.framework.pigeon.core.exception.ReplicateException;

public interface Reproducible<T extends Reproducible<T>> {

	T replicate() throws ReplicateException;

}
