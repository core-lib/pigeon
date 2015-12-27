package payne.framework.pigeon.core.formatting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;
import payne.framework.pigeon.core.exception.FormatterException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class FormInvocationFormatter extends URLInvocationFormatter implements InvocationFormatter {

	public String algorithm() {
		return "application/x-www-form-urlencoded";
	}

	public Invocation deserialize(Header header, InputStream in, String charset, Method method) throws FormatterException {
		try {
			String parameters = IOToolkit.toString(in);
			parameters = URLDecoder.decode(parameters, Charset.defaultCharset().name());
			in = new ByteArrayInputStream(parameters.getBytes(Charset.defaultCharset().name()));
			return super.deserialize(header, in, charset, method);
		} catch (IOException e) {
			throw new FormatterException(e, this, in, method);
		}
	}

}
