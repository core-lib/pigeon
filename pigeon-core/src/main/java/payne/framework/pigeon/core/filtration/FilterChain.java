package payne.framework.pigeon.core.filtration;

public interface FilterChain<T> {

	void go(T target) throws Exception;

}