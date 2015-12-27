package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import payne.framework.pigeon.core.exception.CompressorException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class LZ4InvocationCompressor implements InvocationCompressor {

	public String algorithm() {
		return "LZ4";
	}

	public void compress(InputStream in, OutputStream out) throws CompressorException {
		LZ4BlockOutputStream _out = null;
		try {
			_out = new LZ4BlockOutputStream(out);
			IOToolkit.transmit(in, _out);
		} catch (IOException e) {
			throw new CompressorException(e);
		} finally {
			IOToolkit.close(_out);
		}
	}

	public void decompress(InputStream in, OutputStream out) throws CompressorException {
		LZ4BlockInputStream _in = null;
		try {
			_in = new LZ4BlockInputStream(in);
			IOToolkit.transmit(_in, out);
		} catch (IOException e) {
			throw new CompressorException(e);
		} finally {
			IOToolkit.close(_in);
		}
	}

	public OutputStream wrap(OutputStream outputStream) throws Exception {
		return new LZ4InvocationOutputStream(outputStream);
	}

	public InputStream wrap(InputStream inputStream) throws Exception {
		return new LZ4InvocationInputStream(inputStream);
	}

}
