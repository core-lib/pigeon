package payne.framework.pigeon.core.encryption;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.InvocationInputStream;

public class SignInvocationInputStream extends InvocationInputStream {
	private final Signature signature;
	private final InputStream in;
	private final InvocationEncoder dataEncoder;
	private final InvocationEncoder signatureEncoder;
	private final byte separator;

	private byte[] chunk = new byte[0];
	private int position = 0;
	private int total = 0;

	private boolean completed;
	private Boolean verified;

	public SignInvocationInputStream(Signature signature, InputStream in, InvocationEncoder dataEncoder, InvocationEncoder signatureEncoder, byte separator) throws NoSuchAlgorithmException {
		super();
		this.signature = signature;
		this.in = in;
		this.dataEncoder = dataEncoder;
		this.signatureEncoder = signatureEncoder;
		this.separator = separator;
	}

	@Override
	public int read() throws IOException {
		if (position < chunk.length) {
			int b = chunk[position++] & 0xff;
			try {
				signature.update((byte) b);
			} catch (SignatureException e) {
				throw new IOException(e);
			}
			return b;
		} else if (verified != null) {
			if (verified) {
				return -1;
			} else {
				throw new IOException("message has been tampered!");
			}
		} else if (completed) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IOToolkit.transmit(in, out);
			try {
				if (signature.verify(signatureEncoder.decode(out.toByteArray()))) {
					verified = true;
					return -1;
				} else {
					verified = false;
					throw new IOException("message has been tampered!");
				}
			} catch (SignatureException e) {
				throw new IOException(e);
			}
		} else {
			// 重新读取一块
			total = 0;
			position = 0;
			byte[] bytes = dataEncoder.chunk(false);
			for (int i = 0; i < bytes.length; i++) {
				int b = in.read();
				if (b == separator) {
					completed = true;
					break;
				} else {
					bytes[i] = (byte) b;
					total++;
				}
			}
			chunk = dataEncoder.decode(Arrays.copyOf(bytes, total));
			return read();
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
