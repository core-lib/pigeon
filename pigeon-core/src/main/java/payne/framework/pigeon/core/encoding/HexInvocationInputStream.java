package payne.framework.pigeon.core.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

import payne.framework.pigeon.core.toolkit.InvocationInputStream;

public class HexInvocationInputStream extends InvocationInputStream {
	private final InputStream in;

	private byte[] chunk = new byte[0];
	private int position = 0;

	private boolean completed = false;

	public HexInvocationInputStream(InputStream in) {
		super();
		this.in = in;
	}

	@Override
	public int read() throws IOException {
		if (completed) {
			return -1;
		} else if (position == chunk.length) {
			chunk = new byte[1024];
			int length = in.read(chunk);
			if (length == -1) {
				completed = true;
				return read();
			}
			chunk = Hex.decode(Arrays.copyOf(chunk, length));
			position = 0;
			return read();
		} else {
			return chunk[position++] & 0xff;
		}
	}

}
