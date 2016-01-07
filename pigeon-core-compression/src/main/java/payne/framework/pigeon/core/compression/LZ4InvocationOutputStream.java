package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.OutputStream;

import net.jpountz.lz4.LZ4BlockOutputStream;

public class LZ4InvocationOutputStream extends OutputStream {
	private final LZ4BlockOutputStream out;

	public LZ4InvocationOutputStream(OutputStream out) {
		super();
		this.out = new LZ4BlockOutputStream(out);
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
