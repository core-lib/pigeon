package payne.framework.pigeon.core.signature;

import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.toolkit.InvocationOutputStream;

public class SignInvocationOutputStream extends InvocationOutputStream {
	private final Signature signature;
	private final OutputStream out;
	private final InvocationEncoder dataEncoder;
	private final InvocationEncoder signatureEncoder;
	private final byte separator;

	private boolean closed;

	private byte[] chunk;
	private int position;

	public SignInvocationOutputStream(Signature signature, OutputStream out, InvocationEncoder dataEncoder, InvocationEncoder signatureEncoder, byte separator) throws NoSuchAlgorithmException {
		super();
		this.signature = signature;
		this.out = out;
		this.dataEncoder = dataEncoder;
		this.signatureEncoder = signatureEncoder;
		this.separator = separator;

		this.chunk = dataEncoder.chunk(true);
		this.position = 0;
	}

	@Override
	public void write(int b) throws IOException {
		chunk[position++] = (byte) b;
		try {
			signature.update((byte) b);
		} catch (SignatureException e) {
			throw new IOException(e);
		}
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
			out.write(signatureEncoder.encode(signature.sign()));

			out.flush();
		} catch (SignatureException e) {
			throw new IOException(e);
		} finally {
			closed = true;
			out.close();
		}
	}

}
