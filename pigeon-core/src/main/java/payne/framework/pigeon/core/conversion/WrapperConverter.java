package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import payne.framework.pigeon.core.exception.ConverterException;

public class WrapperConverter implements Converter {
	private static final List<Class<?>> CLASSES = new ArrayList<Class<?>>();

	static {
		CLASSES.add(Boolean.class);
		CLASSES.add(Byte.class);
		CLASSES.add(Short.class);
		CLASSES.add(Character.class);
		CLASSES.add(Integer.class);
		CLASSES.add(Long.class);
		CLASSES.add(Float.class);
		CLASSES.add(Double.class);
	}

	public boolean supports(Class<?> clazz) {
		return CLASSES.contains(clazz);
	}

	public <T> T convert(String name, Class<T> clazz, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		String[] values = map.get(name);
		String value = values != null && values.length > 0 ? values[0] : null;
		if (value == null) {
			return null;
		}
		Object result = null;
		switch (CLASSES.indexOf(clazz)) {
		case 0:
			result = Boolean.valueOf(value);
			break;
		case 1:
			result = Byte.valueOf(value);
			break;
		case 2:
			result = Short.valueOf(value);
			break;
		case 3:
			result = Character.valueOf(value.charAt(0));
			break;
		case 4:
			result = Integer.valueOf(value);
			break;
		case 5:
			result = Long.valueOf(value);
			break;
		case 6:
			result = Float.valueOf(value);
			break;
		case 7:
			result = Double.valueOf(value);
			break;
		default:
			throw new ConverterException("can not converter class " + clazz + " with " + this.getClass(), name, clazz, map, provider);
		}
		return clazz.cast(result);
	}

	public boolean supports(ParameterizedType type) {
		return false;
	}

	public Object convert(String name, ParameterizedType type, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		throw new UnsupportedOperationException("converter of " + this.getClass() + " do not supported parameterized type");
	}

}
