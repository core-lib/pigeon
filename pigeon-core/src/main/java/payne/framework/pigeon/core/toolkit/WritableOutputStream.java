package payne.framework.pigeon.core.toolkit;

import java.io.IOException;
import java.io.OutputStream;

public class WritableOutputStream extends OutputStream {
	private final Writable writable;

	public WritableOutputStream(Writable writable) {
		super();
		this.writable = writable;
	}

	@Override
	public void write(int b) throws IOException {
		writable.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		writable.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		writable.write(b, off, len);
	}

}
