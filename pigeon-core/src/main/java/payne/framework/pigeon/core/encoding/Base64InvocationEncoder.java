package payne.framework.pigeon.core.encoding;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import payne.framework.pigeon.core.exception.EncoderException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class Base64InvocationEncoder implements InvocationEncoder {

	public String algorithm() {
		return "base64";
	}

	public byte[] chunk(boolean encode) {
		return new byte[(encode ? 6 : 8) * 100];
	}

	public void encode(InputStream in, OutputStream out) throws EncoderException {
		try {
			IOToolkit.transmit(new Base64InputStream(in, true), out);
		} catch (Exception e) {
			throw new EncoderException(e);
		}
	}

	public void decode(InputStream in, OutputStream out) throws EncoderException {
		try {
			IOToolkit.transmit(in, new Base64OutputStream(out, false));
		} catch (Exception e) {
			throw new EncoderException(e);
		}
	}

	public byte[] encode(byte[] bytes) {
		return new Base64().encode(bytes);
	}

	public byte[] decode(byte[] bytes) {
		return new Base64().decode(bytes);
	}

	public OutputStream wrap(OutputStream outputStream) throws Exception {
		return new Base32InvocationOutputStream(outputStream);
	}

	public InputStream wrap(InputStream inputStream) throws Exception {
		return new Base32InvocationInputStream(inputStream);
	}

}
