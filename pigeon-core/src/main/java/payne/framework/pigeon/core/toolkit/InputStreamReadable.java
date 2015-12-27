package payne.framework.pigeon.core.toolkit;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamReadable implements Readable {

	private final InputStream inputStream;

	public InputStreamReadable(InputStream inputStream) {
		super();
		this.inputStream = inputStream;
	}

	public int read() throws IOException {
		return inputStream.read();
	}

	public int read(byte[] b) throws IOException {
		return inputStream.read(b);
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return inputStream.read(b, off, len);
	}

	public InputStream getInputStream() {
		return inputStream;
	}

}