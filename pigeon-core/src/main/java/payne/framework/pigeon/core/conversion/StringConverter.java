package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import payne.framework.pigeon.core.exception.ConverterException;

public class StringConverter implements Converter {

	public boolean supports(Class<?> clazz) {
		return clazz == String.class;
	}

	public <T> T convert(String name, Class<T> clazz, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		String[] values = map.get(name);
		return clazz.cast(values != null && values.length > 0 ? values[0] : null);
	}

	public boolean supports(ParameterizedType type) {
		return false;
	}

	public Object convert(String name, ParameterizedType type, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		throw new UnsupportedOperationException("converter of " + this.getClass() + " do not supported parameterized type");
	}

}
