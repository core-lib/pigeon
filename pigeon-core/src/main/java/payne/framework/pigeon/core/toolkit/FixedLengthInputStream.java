package payne.framework.pigeon.core.toolkit;

import java.io.IOException;
import java.io.InputStream;

public class FixedLengthInputStream extends InputStream {
	private final InputStream inputStream;
	private final int total;
	private int position;

	public FixedLengthInputStream(InputStream inputStream, int total) {
		super();
		this.inputStream = inputStream;
		this.total = total;
	}

	@Override
	public int read() throws IOException {
		if (position >= total) {
			return -1;
		}
		position++;
		return inputStream.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (position >= total) {
			return -1;
		}
		len = Math.min(b.length - off, len);
		len = Math.min(len, total - position);
		len = inputStream.read(b, off, len);
		position += len;
		return len;
	}

}
