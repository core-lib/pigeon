package payne.framework.pigeon.core.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

import payne.framework.pigeon.core.exception.EncoderException;

public class HexInvocationEncoder implements InvocationEncoder {

	public String algorithm() {
		return "hex";
	}

	public byte[] chunk(boolean encode) {
		return new byte[(encode ? 1 : 2) * 1000];
	}

	public void encode(InputStream in, OutputStream out) throws EncoderException {
		try {
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				out.write(Hex.encode(buffer, 0, length));
			}
		} catch (IOException e) {
			throw new EncoderException(e);
		}
	}

	public void decode(InputStream in, OutputStream out) throws EncoderException {
		try {
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				out.write(Hex.decode(Arrays.copyOf(buffer, length)));
			}
		} catch (IOException e) {
			throw new EncoderException(e);
		}
	}

	public byte[] encode(byte[] bytes) {
		return Hex.encode(bytes);
	}

	public byte[] decode(byte[] bytes) {
		return Hex.decode(bytes);
	}

	public OutputStream wrap(OutputStream outputStream) throws Exception {
		return new HexInvocationOutputStream(outputStream);
	}

	public InputStream wrap(InputStream inputStream) throws Exception {
		return new HexInvocationInputStream(inputStream);
	}

}
