package payne.framework.pigeon.core.toolkit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Collections {

	public static String concatenate(Iterable<?> iterable, String separator, String... excludes) {
		List<String> _excludes = Arrays.asList(excludes != null ? excludes : new String[0]);
		StringBuilder builder = new StringBuilder();
		Iterator<?> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			String element = String.valueOf(iterator.next());
			if (_excludes.contains(element)) {
				continue;
			}
			builder.append(element);
			if (iterator.hasNext()) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	public static <T> String concatenate(T[] array, String separator, String... excludes) {
		return concatenate(Arrays.asList(array), separator, excludes);
	}

	public static boolean containsAll(Collection<?> collection, Object... elements) {
		for (Object element : elements) {
			if (collection.contains(element)) {
				continue;
			}
			return false;
		}
		return true;
	}

	public static boolean containsAny(Collection<?> collection, Object... elements) {
		for (Object element : elements) {
			if (collection.contains(element)) {
				return true;
			}
		}
		return false;
	}
	
}
