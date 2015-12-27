package payne.framework.pigeon.core.toolkit;

import java.io.IOException;
import java.io.OutputStream;

public class FixedLengthOutputStream extends OutputStream {
	private final OutputStream outputStream;
	private final int total;
	private int position;

	public FixedLengthOutputStream(OutputStream outputStream, int total) {
		super();
		this.outputStream = outputStream;
		this.total = total;
	}

	@Override
	public void write(int b) throws IOException {
		if (position >= total) {
			return;
		}
		outputStream.write(b);
		position++;
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (position >= total) {
			return;
		}
		len = Math.min(b.length - off, len);
		len = Math.min(len, total - position);
		outputStream.write(b, off, len);
		position += len;
	}

	@Override
	public void flush() throws IOException {
		outputStream.flush();
	}

}
