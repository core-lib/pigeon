package payne.framework.pigeon.core.formatting;

import java.io.IOException;
import java.io.OutputStream;

public class FormatInvocationOutputStream extends OutputStream {
	private final InvocationFormatter formatter;
	private final OutputStream out;
	private final String charset;

	public FormatInvocationOutputStream(InvocationFormatter formatter, OutputStream out, String charset) {
		super();
		this.formatter = formatter;
		this.out = out;
		this.charset = charset;
	}

	@Override
	public void write(int b) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void serialize(Object data) throws IOException {
		formatter.serialize(data, out, charset);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	public void close() throws IOException {
		out.close();
	}

}
