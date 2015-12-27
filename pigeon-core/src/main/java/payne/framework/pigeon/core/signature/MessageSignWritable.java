package payne.framework.pigeon.core.signature;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Signature;
import java.security.SignatureException;

import payne.framework.pigeon.core.toolkit.Writable;

public class MessageSignWritable implements Writable {
	private final Signature signature;
	private final OutputStream outputStream;

	public MessageSignWritable(Signature signature) {
		super();
		this.signature = signature;
		this.outputStream = null;
	}

	public MessageSignWritable(Signature signature, OutputStream outputStream) {
		super();
		this.signature = signature;
		this.outputStream = outputStream;
	}

	public int write(int b) throws IOException {
		return write(new byte[] { (byte) b });
	}

	public int write(byte[] b) throws IOException {
		return write(b, 0, b.length);
	}

	public int write(byte[] b, int off, int len) throws IOException {
		try {
			signature.update(b, off, len);
		} catch (SignatureException e) {
			throw new IOException(e);
		}
		if (outputStream != null) {
			outputStream.write(b, off, len);
		}
		return len;
	}

	public Signature getSignature() {
		return signature;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

}
