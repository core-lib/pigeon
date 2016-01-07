package payne.framework.pigeon.core.encoding;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

import payne.framework.pigeon.core.encoding.exception.EncoderException;

public class HexInvocationEncoder implements InvocationEncoder {

	public String algorithm() {
		return "hex";
	}

	public byte[] chunk(boolean encode) {
		return new byte[(encode ? 1 : 2) * 1000];
	}

	public void encode(InputStream in, OutputStream out) throws EncoderException {
		try {
			Hex hex = new Hex();
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				out.write(hex.encode(Arrays.copyOf(buffer, length)));
			}
		} catch (Exception e) {
			throw new EncoderException(e);
		}
	}

	public void decode(InputStream in, OutputStream out) throws EncoderException {
		try {
			Hex hex = new Hex();
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = in.read(buffer)) != -1) {
				out.write(hex.decode(Arrays.copyOf(buffer, length)));
			}
		} catch (Exception e) {
			throw new EncoderException(e);
		}
	}

	public byte[] encode(byte[] bytes) {
		try {
			return new Hex().encode(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] decode(byte[] bytes) {
		try {
			return new Hex().decode(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public OutputStream wrap(OutputStream outputStream) throws Exception {
		return new HexInvocationOutputStream(outputStream);
	}

	public InputStream wrap(InputStream inputStream) throws Exception {
		return new HexInvocationInputStream(inputStream);
	}

}
