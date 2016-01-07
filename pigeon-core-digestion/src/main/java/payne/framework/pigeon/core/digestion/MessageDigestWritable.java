package payne.framework.pigeon.core.digestion;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;

import payne.framework.pigeon.core.toolkit.Writable;

public class MessageDigestWritable implements Writable {
	private final MessageDigest messageDigest;
	private final OutputStream outputStream;

	public MessageDigestWritable(MessageDigest messageDigest) {
		super();
		this.messageDigest = messageDigest;
		this.outputStream = null;
	}

	public MessageDigestWritable(MessageDigest messageDigest, OutputStream outputStream) {
		super();
		this.messageDigest = messageDigest;
		this.outputStream = outputStream;
	}

	public int write(int b) throws IOException {
		return write(new byte[] { (byte) b });
	}

	public int write(byte[] b) throws IOException {
		return write(b, 0, b.length);
	}

	public int write(byte[] b, int off, int len) throws IOException {
		messageDigest.update(b, off, len);
		if (outputStream != null) {
			outputStream.write(b, off, len);
		}
		return len;
	}

	public MessageDigest getMessageDigest() {
		return messageDigest;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}
}
