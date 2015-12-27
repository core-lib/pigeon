package payne.framework.pigeon.core.formatting;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;

public class FormatInvocationInputStream extends InputStream {
	private final InvocationFormatter formatter;
	private final Header header;
	private final InputStream in;
	private final String charset;
	private final Method method;

	public FormatInvocationInputStream(InvocationFormatter formatter, Header header, InputStream in, String charset, Method method) {
		super();
		this.formatter = formatter;
		this.header = header;
		this.in = in;
		this.charset = charset;
		this.method = method;
	}

	@Override
	public int read() throws IOException {
		throw new UnsupportedOperationException();
	}

	public Invocation deserialize() throws IOException {
		return formatter.deserialize(header, in, charset, method);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
