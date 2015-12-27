package payne.framework.pigeon.core.toolkit;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamWritable implements Writable {
	private final OutputStream outputStream;

	public OutputStreamWritable(OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
	}

	public int write(int b) throws IOException {
		outputStream.write(b);
		return 1;
	}

	public int write(byte[] b) throws IOException {
		outputStream.write(b);
		return b.length;
	}

	public int write(byte[] b, int off, int len) throws IOException {
		len = Math.min(b.length - off, len);
		outputStream.write(b, off, len);
		return len;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

}