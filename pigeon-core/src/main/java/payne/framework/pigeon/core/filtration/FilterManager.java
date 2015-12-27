package payne.framework.pigeon.core.filtration;

import java.util.Iterator;

public interface FilterManager<T, R extends FilterManager<T, R>> {

	R add(Filter<T> filter);

	R remove(Filter<T> filter);

	Iterator<Filter<T>> filters();

}
