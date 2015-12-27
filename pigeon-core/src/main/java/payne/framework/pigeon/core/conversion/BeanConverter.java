package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import payne.framework.pigeon.core.exception.ConverterException;

import com.googlecode.openbeans.PropertyDescriptor;

public class BeanConverter implements Converter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public boolean supports(Class<?> clazz) {
		return true;
	}

	public <T> T convert(String name, Class<T> clazz, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		T bean = null;
		try {
			bean = clazz.newInstance();
		} catch (Exception e) {
			throw new ConverterException(e, name, clazz, map, provider);
		}
		for (String key : map.keySet()) {
			if (key.startsWith(name + ".") && key.length() > name.length() + 1) {
				int index = key.indexOf('.');
				String property = key.substring(index + 1);
				String[] values = map.get(key) != null ? map.get(key) : new String[0];
				Map<String, String[]> _map = new HashMap<String, String[]>();
				_map.put(property, values);
				try {
					PropertyDescriptor descriptor = new PropertyDescriptor(property.split("\\.")[0], clazz);
					Object value = provider.convert(property.split("\\.")[0], descriptor.getPropertyType(), _map);
					descriptor.getWriteMethod().invoke(bean, value);
				} catch (Exception e) {
					logger.warn("property {} is unfound from class {}", property, clazz);
				}
			}
		}
		return bean;
	}

	public boolean supports(ParameterizedType type) {
		return type.getRawType() instanceof Class<?>;
	}

	public Object convert(String name, ParameterizedType type, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		Class<?> clazz = (Class<?>) type.getRawType();
		Object bean = null;
		try {
			bean = clazz.newInstance();
		} catch (Exception e) {
			throw new ConverterException(e, name, type, map, provider);
		}
		List<?> variables = Arrays.asList(clazz.getTypeParameters());
		for (String key : map.keySet()) {
			if (key.startsWith(name + ".") && key.length() > name.length() + 1) {
				int index = key.indexOf('.');
				String property = key.substring(index + 1);
				String[] values = map.get(key) != null ? map.get(key) : new String[0];
				Map<String, String[]> _map = new HashMap<String, String[]>();
				_map.put(property, values);
				try {
					PropertyDescriptor descriptor = new PropertyDescriptor(property.split("\\.")[0], clazz);
					Type _type = descriptor.getReadMethod().getGenericReturnType();
					if (_type instanceof TypeVariable<?>) {
						int _index = variables.indexOf(_type);
						_type = type.getActualTypeArguments()[_index];
					}
					Object value = provider.convert(property.split("\\.")[0], _type, _map);
					descriptor.getWriteMethod().invoke(bean, value);
				} catch (Exception e) {
					logger.warn("property {} is unfound from class {}", property, clazz);
				}
			}
		}
		return bean;
	}

}
