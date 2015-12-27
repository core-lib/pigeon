package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import payne.framework.pigeon.core.exception.CompressorException;
import payne.framework.pigeon.core.toolkit.IOToolkit;

public class GZIPInvocationCompressor implements InvocationCompressor {

	public String algorithm() {
		return "GZIP";
	}

	public void compress(InputStream in, OutputStream out) throws CompressorException {
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(out);
			IOToolkit.transmit(in, gos);
		} catch (IOException e) {
			throw new CompressorException(e);
		} finally {
			IOToolkit.close(gos);
		}
	}

	public void decompress(InputStream in, OutputStream out) throws CompressorException {
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(in);
			IOToolkit.transmit(gis, out);
		} catch (IOException e) {
			throw new CompressorException(e);
		} finally {
			IOToolkit.close(gis);
		}
	}

	public OutputStream wrap(OutputStream outputStream) throws Exception {
		return new GZIPInvocationOutputStream(outputStream);
	}

	public InputStream wrap(InputStream inputStream) throws Exception {
		return new GZIPInvocationInputStream(inputStream);
	}
	
}
