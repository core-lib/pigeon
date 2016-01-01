package payne.framework.pigeon.core.formatting;

import java.io.IOException;
import java.io.InputStream;

public class FormatInvocationInputStream extends InputStream {
	private final InvocationFormatter formatter;
	private final Structure structure;
	private final InputStream in;
	private final String charset;

	public FormatInvocationInputStream(InvocationFormatter formatter, Structure structure, InputStream in, String charset) {
		super();
		this.formatter = formatter;
		this.structure = structure;
		this.in = in;
		this.charset = charset;
	}

	@Override
	public int read() throws IOException {
		throw new UnsupportedOperationException();
	}

	public Object deserialize() throws IOException {
		return formatter.deserialize(structure, in, charset);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
