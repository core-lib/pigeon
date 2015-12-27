package payne.framework.pigeon.core.formatting;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.core.dynamic.DynamicInvocation;
import payne.framework.pigeon.core.dynamic.DynamicInvocationClassLoader;
import payne.framework.pigeon.core.exception.FormatterException;
import payne.framework.pigeon.core.toolkit.Collections;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;

public class AVROInvocationFormatter implements InvocationFormatter {
	private final AvroMapper mapper;

	public AVROInvocationFormatter() {
		this.mapper = new AvroMapper();
		this.mapper.setAnnotationIntrospector(new DefaultJacksonAnnotationIntrospector(mapper));
		this.mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		this.mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public AVROInvocationFormatter(AvroMapper mapper) {
		this.mapper = mapper;
	}

	public String algorithm() {
		return "application/avro";
	}

	public void serialize(Header header, Invocation data, OutputStream out, String charset) throws FormatterException {
		try {
			String className = Collections.concatenate(data.getPath().split("\\/+"), ".", "");
			Class<? extends DynamicInvocation> clazz = DynamicInvocationClassLoader.getInstance().load(className);
			DynamicInvocation invoke = clazz.getConstructor(Invocation.class).newInstance(data);
			this.mapper.writer(this.mapper.schemaFor(clazz)).writeValue(out, invoke);
		} catch (Exception e) {
			throw new FormatterException(e, this, data, null);
		}
	}

	public Invocation deserialize(Header header, InputStream in, String charset, Method method) throws FormatterException {
		try {
			String className = Collections.concatenate(Pigeons.getOpenPath(method).split("\\/+"), ".", "");
			Class<? extends DynamicInvocation> clazz = DynamicInvocationClassLoader.getInstance().load(className);
			DynamicInvocation invoke = this.mapper.reader(this.mapper.schemaFor(clazz)).forType(clazz).readValue(in);
			return invoke.toInvocation();
		} catch (Exception e) {
			throw new FormatterException(e, this, in, method);
		}
	}

}
