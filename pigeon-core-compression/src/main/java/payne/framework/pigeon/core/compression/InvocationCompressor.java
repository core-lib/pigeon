package payne.framework.pigeon.core.compression;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Conversion;
import payne.framework.pigeon.core.compression.exception.CompressorException;

public interface InvocationCompressor extends Conversion {

	void compress(InputStream in, OutputStream out) throws CompressorException;

	void decompress(InputStream in, OutputStream out) throws CompressorException;
	
	OutputStream wrap(OutputStream outputStream) throws Exception;

	InputStream wrap(InputStream inputStream) throws Exception;

}
