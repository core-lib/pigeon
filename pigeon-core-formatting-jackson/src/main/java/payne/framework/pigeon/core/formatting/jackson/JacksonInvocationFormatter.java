package payne.framework.pigeon.core.formatting.jackson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;

import payne.framework.pigeon.core.exception.FormatterException;
import payne.framework.pigeon.core.formatting.InvocationFormatter;
import payne.framework.pigeon.core.formatting.Structure;
import payne.framework.pigeon.core.formatting.Structure.Form;
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

	public void serialize(Object data, Structure structure, OutputStream out, String charset) throws FormatterException {
		OutputStreamWriter osw = null;
		try {
			if (structure.form == Form.ARRAY) {
				switch (structure.types.length) {
				case 0:
					data = null;
					break;
				case 1:
					data = data != null && data.getClass().isArray() && Array.getLength(data) == 1 ? Array.get(data, 0) : data;
					break;
				}
			}
			if (transcoding) {
				osw = new OutputStreamWriter(out, charset);
				this.mapper.writeValue(osw, data);
			} else {
				this.mapper.writeValue(out, data);
			}
		} catch (Exception e) {
			throw new FormatterException(e, this, data);
		} finally {
			IOToolkit.close(osw);
		}
	}

	public Object deserialize(Structure structure, InputStream in, String charset) throws FormatterException {
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
			}

			switch (structure.form) {
			case VALUE:
				Object result = null;
				if (structure.types[0] != Void.TYPE) {
					result = this.mapper.readValue(node.traverse(), this.mapper.constructType(structure.types[0]));
				}
				return result;
			case ARRAY:
				Object[] arguments = new Object[structure.types.length];
				if (node.isArray() == false && structure.types.length > 0) {
					arguments[0] = this.mapper.readValue(node.traverse(), this.mapper.constructType(structure.types[0]));
				}
				for (int i = 0; node.isArray() && i < structure.types.length; i++) {
					arguments[i] = this.mapper.readValue(node.get(i).traverse(), this.mapper.constructType(structure.types[i]));
				}
				return arguments;
			default:
				return null;
			}
		} catch (Exception e) {
			throw new FormatterException(e, this, in, structure);
		} finally {
			IOToolkit.close(isr);
		}
	}
}
