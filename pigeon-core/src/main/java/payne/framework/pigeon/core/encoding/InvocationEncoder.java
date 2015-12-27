package payne.framework.pigeon.core.encoding;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Conversion;
import payne.framework.pigeon.core.exception.EncoderException;

public interface InvocationEncoder extends Conversion {

	byte[] chunk(boolean encode);

	void encode(InputStream in, OutputStream out) throws EncoderException;

	void decode(InputStream in, OutputStream out) throws EncoderException;

	byte[] encode(byte[] bytes);

	byte[] decode(byte[] bytes);

	OutputStream wrap(OutputStream outputStream) throws Exception;

	InputStream wrap(InputStream inputStream) throws Exception;

}
