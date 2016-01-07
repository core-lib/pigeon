package payne.framework.pigeon.core.digestion;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Conversion;
import payne.framework.pigeon.core.digestion.exception.DigesterException;
import payne.framework.pigeon.core.encoding.InvocationEncoder;

public interface InvocationDigester extends Conversion {

	byte[] digest(byte[] bytes) throws DigesterException;

	byte[] digest(byte[] bytes, int offset, int length) throws DigesterException;

	byte[] digest(InputStream in) throws DigesterException;

	byte[] digest(InputStream in, OutputStream out) throws DigesterException;

	OutputStream wrap(OutputStream outputStream, InvocationEncoder dataEncoder, InvocationEncoder digestEncoder, byte separator) throws Exception;

	InputStream wrap(InputStream inputStream, InvocationEncoder dataEncoder, InvocationEncoder digestEncoder, byte separator) throws Exception;

}
