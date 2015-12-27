package payne.framework.pigeon.core.exception;

import java.lang.reflect.Type;
import java.util.Map;

import payne.framework.pigeon.core.conversion.ConversionProvider;

public class ConverterException extends Exception {
	private static final long serialVersionUID = 5467589843078783058L;

	private final String name;
	private final Type type;
	private final Map<String, String[]> map;
	private final ConversionProvider provider;

	public ConverterException(String name, Type type, Map<String, String[]> map, ConversionProvider provider) {
		this(null, null, name, type, map, provider);
	}

	public ConverterException(String message, String name, Type type, Map<String, String[]> map, ConversionProvider provider) {
		this(message, null, name, type, map, provider);
	}

	public ConverterException(Throwable cause, String name, Type type, Map<String, String[]> map, ConversionProvider provider) {
		this(null, cause, name, type, map, provider);
	}

	public ConverterException(String message, Throwable cause, String name, Type type, Map<String, String[]> map, ConversionProvider provider) {
		super(message, cause);
		this.name = name;
		this.type = type;
		this.map = map;
		this.provider = provider;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public Map<String, String[]> getMap() {
		return map;
	}

	public ConversionProvider getProvider() {
		return provider;
	}

}
