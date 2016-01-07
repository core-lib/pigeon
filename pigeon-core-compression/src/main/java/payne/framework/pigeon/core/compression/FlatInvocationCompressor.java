package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;

import payne.framework.pigeon.core.compression.exception.CompressorException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class FlatInvocationCompressor implements InvocationCompressor {

	public String algorithm() {
		return "flat";
	}

	public void compress(InputStream in, OutputStream out) throws CompressorException {
		DeflaterInputStream dis = null;
		try {
			dis = new DeflaterInputStream(in);
			IOToolkit.transmit(dis, out);
		} catch (IOException e) {
			throw new CompressorException(e);
		} finally {
			IOToolkit.close(dis);
		}
	}

	public void decompress(InputStream in, OutputStream out) throws CompressorException {
		InflaterInputStream iis = null;
		try {
			iis = new InflaterInputStream(in);
			IOToolkit.transmit(iis, out);
		} catch (IOException e) {
			throw new CompressorException(e);
		} finally {
			IOToolkit.close(iis);
		}
	}

	public OutputStream wrap(OutputStream outputStream) throws Exception {
		return new FlatInvocationOutputStream(outputStream);
	}

	public InputStream wrap(InputStream inputStream) throws Exception {
		return new FlatInvocationInputStream(inputStream);
	}

}
