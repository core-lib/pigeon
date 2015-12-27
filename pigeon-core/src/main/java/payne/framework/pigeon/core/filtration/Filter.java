package payne.framework.pigeon.core.filtration;

public interface Filter<T> {

	void filtrate(T target, FilterChain<T> chain) throws Exception;

}
