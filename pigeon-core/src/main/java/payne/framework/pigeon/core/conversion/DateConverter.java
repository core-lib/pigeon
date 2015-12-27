package payne.framework.pigeon.core.conversion;

import java.lang.reflect.ParameterizedType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import payne.framework.pigeon.core.exception.ConverterException;

public class DateConverter implements Converter {
	private static final Map<Pattern, DateFormat> FORMATS = new HashMap<Pattern, DateFormat>();

	static {
		FORMATS.put(Pattern.compile("\\d{1,2}\\/\\d{1,2}\\/\\d{4,}"), new SimpleDateFormat("MM/dd/yyyy"));
		FORMATS.put(Pattern.compile("\\d{4,}\\-\\d{1,2}\\-\\d{1,2}"), new SimpleDateFormat("yyyy-MM-dd"));
		FORMATS.put(Pattern.compile("\\d{4,}\\/\\d{1,2}\\/\\d{1,2}"), new SimpleDateFormat("yyyy/MM/dd"));
		FORMATS.put(Pattern.compile("\\d{4,}\\s\\d{1,2}\\s\\d{1,2}"), new SimpleDateFormat("yyyy MM dd"));
		FORMATS.put(Pattern.compile("\\d{4,}\\.\\d{1,2}\\.\\d{1,2}"), new SimpleDateFormat("yyyy.MM.dd"));
		FORMATS.put(Pattern.compile("\\d{1,2}\\/\\d{1,2}\\/\\d{4,}\\s\\d{1,2}\\:\\d{1,2}\\:\\d{1,2}"), new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"));
		FORMATS.put(Pattern.compile("\\d{4,}\\-\\d{1,2}\\-\\d{1,2}\\s\\d{1,2}\\:\\d{1,2}\\:\\d{1,2}"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		FORMATS.put(Pattern.compile("\\d{4,}\\/\\d{1,2}\\/\\d{1,2}\\s\\d{1,2}\\:\\d{1,2}\\:\\d{1,2}"), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
		FORMATS.put(Pattern.compile("\\d{4,}\\s\\d{1,2}\\s\\d{1,2}\\s\\d{1,2}\\:\\d{1,2}\\:\\d{1,2}"), new SimpleDateFormat("yyyy MM dd HH:mm:ss"));
		FORMATS.put(Pattern.compile("\\d{4,}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}\\:\\d{1,2}\\:\\d{1,2}"), new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"));
	}

	public boolean supports(Class<?> clazz) {
		return Date.class == clazz;
	}

	public <T> T convert(String name, Class<T> clazz, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		String[] values = map.get(name);
		String value = values != null && values.length > 0 ? values[0] : null;
		if (value == null) {
			return null;
		}
		if (value.matches("\\d+")) {
			return clazz.cast(new Date(Long.valueOf(value)));
		}
		for (Entry<Pattern, DateFormat> entry : FORMATS.entrySet()) {
			if (entry.getKey().matcher(value).matches()) {
				try {
					return clazz.cast(entry.getValue().parse(value));
				} catch (ParseException e) {
					throw new ConverterException(e, name, clazz, map, provider);
				}
			}
		}
		throw new ConverterException("unsupported date format of " + value + " please use one of " + FORMATS.keySet(), name, clazz, map, provider);
	}

	public boolean supports(ParameterizedType type) {
		return false;
	}

	public Object convert(String name, ParameterizedType type, Map<String, String[]> map, ConversionProvider provider) throws ConverterException {
		throw new UnsupportedOperationException("converter of " + this.getClass() + " do not supported parameterized type");
	}

}
