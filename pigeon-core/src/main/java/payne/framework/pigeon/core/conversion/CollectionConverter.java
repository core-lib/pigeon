package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import payne.framework.pigeon.core.exception.ConverterException;

public class CollectionConverter implements Converter {
	private static Map<Class<?>, Class<?>> implementations = new HashMap<Class<?>, Class<?>>();

	static {
		implementations.put(Collection.class, List.class);
		implementations.put(List.class, ArrayList.class);
		implementations.put(Set.class, HashSet.class);
		implementations.put(SortedSet.class, NavigableSet.class);
		implementations.put(NavigableSet.class, TreeSet.class);
		implementations.put(Queue.class, PriorityQueue.class);
		implementations.put(Deque.class, ArrayDeque.class);
	}

	public boolean supports(Class<?> clazz) {
		return Collection.class.isAssignableFrom(clazz);
	}

	public <T> T convert(String name, Class<T> clazz, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		String[] values = map.get(name) != null ? map.get(name) : new String[0];
		Class<?> _class = clazz;
		while (implementations.containsKey(_class)) {
			_class = implementations.get(_class);
		}
		try {
			Object collection = _class.getConstructor(Collection.class).newInstance(Arrays.asList(values));
			return clazz.cast(collection);
		} catch (Exception e) {
			throw new ConverterException(e, name, clazz, map, provider);
		}
	}

	public boolean supports(ParameterizedType type) {
		return type.getRawType() instanceof Class<?> && supports((Class<?>) type.getRawType());
	}

	public Object convert(String name, ParameterizedType type, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		Class<?> _class = (Class<?>) type.getRawType();
		while (implementations.containsKey(_class)) {
			_class = implementations.get(_class);
		}
		Collection<Object> collection = new ArrayList<Object>();
		for (String key : map.keySet()) {
			if (key.equals(name)) {
				String[] values = map.get(key);
				Map<String, String[]> _map = new HashMap<String, String[]>();
				String[] _values = new String[1];
				for (String value : values) {
					_values[0] = value;
					_map.put(name, _values);
					collection.add(provider.convert(name, type.getActualTypeArguments()[0], _map));
					_map.clear();
				}
			}
			if (key.startsWith(name + ".") && key.length() > name.length() + 1) {
				String[] values = map.get(key);
				Map<String, String[]> _map = new HashMap<String, String[]>();
				String[] _values = new String[1];
				for (String value : values) {
					_values[0] = value;
					_map.put(key, _values);
					collection.add(provider.convert(name, type.getActualTypeArguments()[0], _map));
					_map.clear();
				}
			}
		}
		try {
			return _class.getConstructor(Collection.class).newInstance(collection);
		} catch (Exception e) {
			throw new ConverterException(e, name, type, map, provider);
		}
	}
}
