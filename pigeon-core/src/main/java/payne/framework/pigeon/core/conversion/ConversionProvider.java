package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import payne.framework.pigeon.core.exception.ConverterException;

public class ConversionProvider {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final Set<Converter> CONVERTERS = new LinkedHashSet<Converter>();

	static {
		CONVERTERS.add(new PrimitiveConverter());
		CONVERTERS.add(new WrapperConverter());
		CONVERTERS.add(new StringConverter());
		CONVERTERS.add(new EnumConverter());
		CONVERTERS.add(new DateConverter());
		CONVERTERS.add(new ArrayConverter());
		CONVERTERS.add(new CollectionConverter());
		CONVERTERS.add(new MapConverter());
		CONVERTERS.add(new BeanConverter());
	}

	public Object convert(String name, Type type, Map<String, String[]> map) throws ConverterException {
		if (type instanceof Class<?>) {
			return convert(name, (Class<?>) type, map);
		}
		if (type instanceof ParameterizedType) {
			return convert(name, (ParameterizedType) type, map);
		}
		throw new ConverterException("unsupported type " + type, name, type, map, this);
	}

	public <T> T convert(String name, Class<T> clazz, Map<String, String[]> map) throws ConverterException {
		for (Converter converter : CONVERTERS) {
			if (converter.supports(clazz)) {
				try {
					return converter.convert(name, clazz, map, this);
				} catch (Exception e) {
					logger.warn("can not convert class {} with name {} using parameters {}", clazz, name, map, e);
					return null;
				}
			}
		}
		throw new ConverterException("unsupported clazz " + clazz, name, clazz, map, this);
	}

	public Object convert(String name, ParameterizedType type, Map<String, String[]> map) throws ConverterException {
		for (Converter converter : CONVERTERS) {
			if (converter.supports(type)) {
				try {
					return converter.convert(name, type, map, this);
				} catch (Exception e) {
					logger.warn("can not convert parameterized type {} with name {} using parameters {}", type, name, map, e);
					return null;
				}
			}
		}
		throw new ConverterException("unsupported parameterized type " + type, name, type, map, this);
	}

}
