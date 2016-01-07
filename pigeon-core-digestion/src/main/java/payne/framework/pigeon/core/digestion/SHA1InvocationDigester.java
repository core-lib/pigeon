package payne.framework.pigeon.core.digestion;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import payne.framework.pigeon.core.digestion.exception.DigesterException;
import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.InputStreamReadable;

public class SHA1InvocationDigester implements InvocationDigester {

	public String algorithm() {
		return "SHA-1";
	}

	public byte[] digest(byte[] bytes) throws DigesterException {
		return digest(bytes, 0, bytes.length);
	}

	public byte[] digest(byte[] bytes, int offset, int length) throws DigesterException {
		try {
			MessageDigest digester = MessageDigest.getInstance("SHA-1");
			digester.update(bytes, offset, length);
			return digester.digest();
		} catch (Exception e) {
			throw new DigesterException(e);
		}
	}

	public byte[] digest(InputStream in) throws DigesterException {
		return digest(in, null);
	}

	public byte[] digest(InputStream in, OutputStream out) throws DigesterException {
		try {
			MessageDigest digester = MessageDigest.getInstance("SHA-1");
			IOToolkit.transmit(new InputStreamReadable(in), new MessageDigestWritable(digester, out));
			return digester.digest();
		} catch (Exception e) {
			throw new DigesterException(e);
		}
	}

	public OutputStream wrap(OutputStream outputStream, InvocationEncoder dataEncoder, InvocationEncoder digestEncoder, byte separator) throws Exception {
		return new DigestInvocationOutputStream(MessageDigest.getInstance(algorithm()), outputStream, dataEncoder, digestEncoder, separator);
	}

	public InputStream wrap(InputStream inputStream, InvocationEncoder dataEncoder, InvocationEncoder digestEncoder, byte separator) throws Exception {
		return new DigestInvocationInputStream(MessageDigest.getInstance(algorithm()), inputStream, dataEncoder, digestEncoder, separator);
	}

}
