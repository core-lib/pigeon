package payne.framework.pigeon.core.formatting;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import payne.framework.pigeon.core.FileWrapper;
import payne.framework.pigeon.core.annotation.rest.Param;
import payne.framework.pigeon.core.conversion.ConversionProvider;
import payne.framework.pigeon.core.exception.FormatterException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class URLInvocationFormatter extends JacksonInvocationFormatter implements InvocationFormatter {
	private ConversionProvider provider = new ConversionProvider();

	public URLInvocationFormatter() {
		super(new MappingJsonFactory(), true);
	}

	public URLInvocationFormatter(ObjectMapper mapper) {
		super(mapper, true);
	}

	public String algorithm() {
		return "application/url";
	}

	@Override
	public void serialize(Object data, OutputStream out, String charset) throws FormatterException {
		OutputStreamWriter osw = null;
		FileInputStream fis = null;
		try {
			if (data instanceof FileWrapper) {
				File file = ((FileWrapper) data).getFile();
				IOToolkit.transmit(fis = new FileInputStream(file), out);
			} else if (data instanceof File) {
				File file = (File) data;
				IOToolkit.transmit(fis = new FileInputStream(file), out);
			} else if (transcoding) {
				osw = new OutputStreamWriter(out, charset);
				this.mapper.writeValue(osw, data);
			} else {
				this.mapper.writeValue(out, data);
			}
		} catch (Exception e) {
			throw new FormatterException(e, this, data, null);
		} finally {
			IOToolkit.close(osw);
			IOToolkit.close(fis);
		}
	}

	public Object deserialize(Structure structure, InputStream in, String charset) throws FormatterException {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(in, charset);
			String parameter = IOToolkit.toString(isr);
			String[] pairs = parameter.split("\\&+");
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			for (String pair : pairs) {
				String[] splits = pair.split("=+");
				String key = splits[0];
				String value = splits.length > 1 ? splits[1] : "";
				if (!parameters.containsKey(key)) {
					parameters.put(key, new String[0]);
				}
				String[] values = parameters.get(key);
				values = Arrays.copyOf(values, values.length + 1);
				values[values.length - 1] = value;
				parameters.put(key, values);
			}
			Object[] arguments = new Object[structure.types.length];
			for (int i = 0; i < structure.types.length; i++) {
				Annotation[] annotations = structure.annotations[i];
				Param param = null;
				for (Annotation annotation : annotations) {
					if (annotation.annotationType() == Param.class) {
						param = (Param) annotation;
						break;
					}
				}
				String prefix = param == null || param.value().trim().equals("") ? "argument" + i : param.value().trim();
				arguments[i] = provider.convert(prefix, structure.types[i], parameters);
			}
			return arguments;
		} catch (Exception e) {
			throw new FormatterException(e, this, in, structure);
		} finally {
			IOToolkit.close(isr);
		}
	}
}
