package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import payne.framework.pigeon.core.exception.ConverterException;

public class MapConverter implements Converter {
	private static Map<Class<?>, Class<?>> implementations = new HashMap<Class<?>, Class<?>>();

	static {
		implementations.put(Map.class, HashMap.class);
		implementations.put(SortedMap.class, NavigableMap.class);
		implementations.put(NavigableMap.class, TreeMap.class);
	}

	public boolean supports(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

	public <T> T convert(String name, Class<T> clazz, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		Map<String, String[]> _map = new LinkedHashMap<String, String[]>();
		for (String key : map.keySet()) {
			if (key.startsWith(name + ".") && key.length() > name.length() + 1) {
				int index = key.indexOf('.');
				String _key = key.substring(index + 1);
				String[] values = map.get(key);
				_map.put(_key, values);
			}
		}
		Class<?> _class = clazz;
		while (implementations.containsKey(_class)) {
			_class = implementations.get(_class);
		}
		try {
			Object result = _class.getConstructor(Map.class).newInstance(_map);
			return clazz.cast(result);
		} catch (Exception e) {
			throw new ConverterException(e, name, clazz, map, provider);
		}
	}

	public boolean supports(ParameterizedType type) {
		return type.getRawType() instanceof Class<?> && supports((Class<?>) type.getRawType());
	}

	public Object convert(String name, ParameterizedType type, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		Map<Object, Object> _map = new LinkedHashMap<Object, Object>();
		for (String key : map.keySet()) {
			if (key.startsWith(name + ".") && key.length() > name.length() + 1) {
				int index = key.indexOf('.');
				String _key = key.substring(index + 1);
				String[] values = map.get(key);

				String __key = _key.split("\\.")[0];

				Map<String, String[]> km = new LinkedHashMap<String, String[]>();
				km.put("key", new String[] { __key });
				Map<String, String[]> vm = new LinkedHashMap<String, String[]>();
				vm.put(_key, values);

				_map.put(provider.convert("key", type.getActualTypeArguments()[0], km), provider.convert(__key, type.getActualTypeArguments()[1], vm));
			}
		}
		Class<?> _class = (Class<?>) type.getRawType();
		while (implementations.containsKey(_class)) {
			_class = implementations.get(_class);
		}
		try {
			return _class.getConstructor(Map.class).newInstance(_map);
		} catch (Exception e) {
			throw new ConverterException(e, name, type, map, provider);
		}
	}

}
