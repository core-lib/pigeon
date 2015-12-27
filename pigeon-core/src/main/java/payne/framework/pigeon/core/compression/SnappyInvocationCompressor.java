package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import payne.framework.pigeon.core.exception.CompressorException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class SnappyInvocationCompressor implements InvocationCompressor {

	public String algorithm() {
		return "snappy";
	}

	public void compress(InputStream in, OutputStream out) throws CompressorException {
		SnappyOutputStream _out = null;
		try {
			_out = new SnappyOutputStream(out);
			IOToolkit.transmit(in, _out);
		} catch (IOException e) {
			throw new CompressorException(e);
		} finally {
			IOToolkit.close(_out);
		}
	}

	public void decompress(InputStream in, OutputStream out) throws CompressorException {
		SnappyInputStream _in = null;
		try {
			_in = new SnappyInputStream(in);
			IOToolkit.transmit(_in, out);
		} catch (IOException e) {
			throw new CompressorException(e);
		} finally {
			IOToolkit.close(_in);
		}
	}

	public OutputStream wrap(OutputStream outputStream) throws Exception {
		return new SnappyInvocationOutputStream(outputStream);
	}

	public InputStream wrap(InputStream inputStream) throws Exception {
		return new SnappyInvocationInputStream(inputStream);
	}

}
