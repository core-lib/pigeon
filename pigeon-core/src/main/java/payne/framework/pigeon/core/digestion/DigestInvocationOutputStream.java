package payne.framework.pigeon.core.digestion;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.toolkit.InvocationOutputStream;

public class DigestInvocationOutputStream extends InvocationOutputStream {
	private final MessageDigest digest;
	private final OutputStream out;
	private final InvocationEncoder dataEncoder;
	private final InvocationEncoder digestEncoder;
	private final byte separator;

	private boolean closed;

	private byte[] chunk;
	private int position;

	public DigestInvocationOutputStream(MessageDigest digest, OutputStream out, InvocationEncoder dataEncoder, InvocationEncoder digestEncoder, byte separator) throws NoSuchAlgorithmException {
		super();
		this.digest = digest;
		this.out = out;
		this.dataEncoder = dataEncoder;
		this.digestEncoder = digestEncoder;
		this.separator = separator;

		this.chunk = dataEncoder.chunk(true);
		this.position = 0;
	}

	@Override
	public void write(int b) throws IOException {
		chunk[position++] = (byte) b;
		digest.update((byte) b);
		if (position == chunk.length) {
			byte[] encoded = dataEncoder.encode(chunk);
			out.write(encoded);
			position = 0;
		}
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		if (closed == true) {
			return;
		}
		try {
			if (position > 0) {
				byte[] encoded = dataEncoder.encode(Arrays.copyOf(chunk, position));
				out.write(encoded);
				position = 0;
			}

			out.write(separator);
			out.write(digestEncoder.encode(digest.digest()));

			out.flush();
		} finally {
			closed = true;
			out.close();
		}
	}

}
