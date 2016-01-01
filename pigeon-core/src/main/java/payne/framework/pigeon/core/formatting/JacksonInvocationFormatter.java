package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.exception.FormatterException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class JacksonInvocationFormatter implements InvocationFormatter {
	protected final ObjectMapper mapper;
	protected final boolean transcoding;

	protected JacksonInvocationFormatter(JsonFactory factory, boolean transcoding) {
		mapper = new ObjectMapper(factory);
		mapper.setAnnotationIntrospector(new DefaultJacksonAnnotationIntrospector(mapper));
		mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
		this.transcoding = transcoding;
	}

	protected JacksonInvocationFormatter(ObjectMapper mapper, boolean transcoding) {
		this.mapper = mapper;
		this.transcoding = transcoding;
	}

	public void serialize(Header header, Object data, OutputStream out, String charset) throws FormatterException {
		OutputStreamWriter osw = null;
		try {
			if (transcoding) {
				osw = new OutputStreamWriter(out, charset);
				this.mapper.writeValue(osw, data);
			} else {
				this.mapper.writeValue(out, data);
			}
		} catch (Exception e) {
			throw new FormatterException(e, this, data, null);
		} finally {
			IOToolkit.close(osw);
		}
	}

	public Object deserialize(Header header, InputStream in, String charset, Method method) throws FormatterException {
		InputStreamReader isr = null;
		try {
			JsonNode node = null;
			if (transcoding) {
				isr = new InputStreamReader(in, charset);
				node = this.mapper.readTree(isr);
			} else {
				node = this.mapper.readTree(in);
			}

			if (node == null || node.isNull()) {
				return null;
			} else if (node.isArray()) {
				Type[] argumentTypes = method.getGenericParameterTypes();
				Object[] arguments = new Object[argumentTypes.length];
				for (int i = 0; i < argumentTypes.length; i++) {
					arguments[i] = this.mapper.readValue(node.get(i).traverse(), this.mapper.constructType(argumentTypes[i]));
				}
				return arguments;
			} else {
				Object result = null;
				Type returnType = method.getGenericReturnType();
				if (returnType != Void.TYPE) {
					result = this.mapper.readValue(node.traverse(), this.mapper.constructType(returnType));
				}
				return result;
			}
		} catch (Exception e) {
			throw new FormatterException(e, this, in, method);
		} finally {
			IOToolkit.close(isr);
		}
	}
}
