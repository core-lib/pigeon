package payne.framework.pigeon.core.formatting;

import java.io.IOException;
import java.io.OutputStream;

import payne.framework.pigeon.core.Header;
import payne.framework.pigeon.core.Invocation;

public class FormatInvocationOutputStream extends OutputStream {
	private final InvocationFormatter formatter;
	private final Header header;
	private final OutputStream out;
	private final String charset;

	public FormatInvocationOutputStream(InvocationFormatter formatter, Header header, OutputStream out, String charset) {
		super();
		this.formatter = formatter;
		this.header = header;
		this.out = out;
		this.charset = charset;
	}

	@Override
	public void write(int b) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void serialize(Invocation invocation) throws IOException {
		formatter.serialize(header, invocation, out, charset);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	public void close() throws IOException {
		out.close();
	}

}
