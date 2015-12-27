package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.InputStream;

import net.jpountz.lz4.LZ4BlockInputStream;

public class LZ4InvocationInputStream extends InputStream {
	private final LZ4BlockInputStream in;

	public LZ4InvocationInputStream(InputStream in) {
		super();
		this.in = new LZ4BlockInputStream(in);
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

}
