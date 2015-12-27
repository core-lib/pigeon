package payne.framework.pigeon.core.toolkit;

import java.io.IOException;
import java.io.InputStream;

public class ReadableInputStream extends InputStream {
	private final Readable readable;

	public ReadableInputStream(Readable readable) {
		super();
		this.readable = readable;
	}

	@Override
	public int read() throws IOException {
		return readable.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return readable.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return readable.read(b, off, len);
	}

}
