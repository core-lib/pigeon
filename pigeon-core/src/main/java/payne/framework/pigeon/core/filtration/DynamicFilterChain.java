package payne.framework.pigeon.core.filtration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DynamicFilterChain<T> implements FilterChain<T>, List<Filter<T>> {
	private final List<Filter<T>> filters;
	private int index = 0;

	public DynamicFilterChain(Collection<Filter<T>> filters) {
		this.filters = new ArrayList<Filter<T>>(filters);
	}

	public void go(T target) throws Exception {
		filters.get(index++).filtrate(target, this);
	}

	public int size() {
		return filters.size();
	}

	public boolean isEmpty() {
		return filters.isEmpty();
	}

	public boolean contains(Object o) {
		return filters.contains(o);
	}

	public Iterator<Filter<T>> iterator() {
		return filters.iterator();
	}

	public Object[] toArray() {
		return filters.toArray();
	}

	public <E> E[] toArray(E[] a) {
		return filters.toArray(a);
	}

	public boolean add(Filter<T> e) {
		return filters.add(e);
	}

	public boolean remove(Object o) {
		return filters.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return filters.containsAll(c);
	}

	public boolean addAll(Collection<? extends Filter<T>> c) {
		return filters.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends Filter<T>> c) {
		return filters.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return filters.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return filters.retainAll(c);
	}

	public void clear() {
		filters.clear();
	}

	public Filter<T> get(int index) {
		return filters.get(index);
	}

	public Filter<T> set(int index, Filter<T> element) {
		return filters.set(index, element);
	}

	public void add(int index, Filter<T> element) {
		filters.add(index, element);
	}

	public Filter<T> remove(int index) {
		return filters.remove(index);
	}

	public int indexOf(Object o) {
		return filters.indexOf(filters);
	}

	public int lastIndexOf(Object o) {
		return filters.lastIndexOf(o);
	}

	public ListIterator<Filter<T>> listIterator() {
		return filters.listIterator();
	}

	public ListIterator<Filter<T>> listIterator(int index) {
		return filters.listIterator(index);
	}

	public List<Filter<T>> subList(int fromIndex, int toIndex) {
		return filters.subList(fromIndex, toIndex);
	}

}
