package payne.framework.pigeon.core.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import payne.framework.pigeon.core.toolkit.InvocationInputStream;

public class HexInvocationInputStream extends InvocationInputStream {
	private final Hex hex = new Hex();
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
			try {
				chunk = hex.decode(Arrays.copyOf(chunk, length));
			} catch (DecoderException e) {
				throw new IOException(e);
			}
			position = 0;
			return read();
		} else {
			return chunk[position++] & 0xff;
		}
	}

}
