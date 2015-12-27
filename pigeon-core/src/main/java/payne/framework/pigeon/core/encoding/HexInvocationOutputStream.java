package payne.framework.pigeon.core.encoding;

import java.io.IOException;
import java.io.OutputStream;

import org.bouncycastle.util.encoders.Hex;

import payne.framework.pigeon.core.toolkit.InvocationOutputStream;

public class HexInvocationOutputStream extends InvocationOutputStream {
	private final byte[] buffer = new byte[1024];
	private int count = 0;
	private final OutputStream out;

	public HexInvocationOutputStream(OutputStream out) {
		super();
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		buffer[count++] = (byte) b;
		if (count == buffer.length) {
			flush();
		}
	}

	@Override
	public void flush() throws IOException {
		if (count > 0) {
			out.write(Hex.encode(buffer, 0, count));
			count = 0;
			out.flush();
		}
	}

	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}

}
