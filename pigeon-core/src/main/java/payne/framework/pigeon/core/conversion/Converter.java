package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import payne.framework.pigeon.core.exception.ConverterException;

public interface Converter {

	boolean supports(Class<?> clazz);

	<T> T convert(String name, Class<T> clazz, Map<String, String[]> map, ConversionProvider provider) throws ConverterException;

	boolean supports(ParameterizedType type);

	Object convert(String name, ParameterizedType type, Map<String, String[]> map, ConversionProvider provider) throws ConverterException;

}
