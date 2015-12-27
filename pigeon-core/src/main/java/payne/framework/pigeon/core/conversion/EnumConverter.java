package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import payne.framework.pigeon.core.exception.ConverterException;

public class EnumConverter implements Converter {

	public boolean supports(Class<?> clazz) {
		return clazz.isEnum();
	}

	public <T> T convert(String name, Class<T> clazz, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		String[] values = map.get(name);
		String value = values != null && values.length > 0 ? values[0] : null;
		if (value == null) {
			return null;
		}
		try {
			Object result = clazz.getMethod("valueOf", String.class).invoke(null, value);
			return clazz.cast(result);
		} catch (Exception e) {
			throw new ConverterException(e, name, clazz, map, provider);
		}
	}

	public boolean supports(ParameterizedType type) {
		return false;
	}

	public Object convert(String name, ParameterizedType type, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		throw new UnsupportedOperationException("converter of " + this.getClass() + " do not supported parameterized type");
	}

}
