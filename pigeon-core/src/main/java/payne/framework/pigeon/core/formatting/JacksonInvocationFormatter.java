package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.exception.FormatterException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

	public void serialize(Header header, Invocation data, OutputStream out, String charset) throws FormatterException {
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

	public Invocation deserialize(Header header, InputStream in, String charset, Method method) throws FormatterException {
		InputStreamReader isr = null;
		try {
			ObjectNode _invocation = null;
			if (transcoding) {
				isr = new InputStreamReader(in, charset);
				_invocation = (ObjectNode) this.mapper.readTree(isr);
			} else {
				_invocation = (ObjectNode) this.mapper.readTree(in);
			}
			JsonNode _arguments = _invocation.remove("arguments");
			JsonNode _result = _invocation.remove("result");
			Invocation invocation = this.mapper.readValue(_invocation.traverse(), Invocation.class);

			Type[] argumentTypes = method.getGenericParameterTypes();
			Object[] arguments = new Object[argumentTypes.length];
			for (int i = 0; _arguments != null && !_arguments.isNull() && i < argumentTypes.length; i++) {
				arguments[i] = this.mapper.readValue(_arguments.get(i).traverse(), this.mapper.constructType(argumentTypes[i]));
			}
			invocation.setArguments(arguments);

			Object result = null;
			Type returnType = method.getGenericReturnType();
			if (returnType != Void.TYPE && _result != null && !_result.isNull()) {
				result = this.mapper.readValue(_result.traverse(), this.mapper.constructType(returnType));
			}
			invocation.setResult(result);

			return invocation;
		} catch (Exception e) {
			throw new FormatterException(e, this, in, method);
		} finally {
			IOToolkit.close(isr);
		}
	}
}
