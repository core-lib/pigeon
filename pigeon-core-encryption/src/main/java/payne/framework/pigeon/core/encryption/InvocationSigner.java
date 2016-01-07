package payne.framework.pigeon.core.encryption;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Conversion;
import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.encryption.exception.SignerException;
import payne.framework.pigeon.core.encryption.key.Key;

public interface InvocationSigner extends Conversion {

	byte[] sign(Key key, byte[] data) throws SignerException;

	byte[] sign(Key key, byte[] data, int offset, int length) throws SignerException;

	byte[] sign(Key key, InputStream in) throws SignerException;

	byte[] sign(Key key, InputStream in, OutputStream out) throws SignerException;

	boolean verify(Key key, byte[] signature, byte[] data) throws SignerException;

	boolean verify(Key key, byte[] signature, byte[] data, int offset, int length) throws SignerException;

	boolean verify(Key key, byte[] signature, InputStream in) throws SignerException;

	boolean verify(Key key, byte[] signature, InputStream in, OutputStream out) throws SignerException;

	OutputStream wrap(Key privateKey, OutputStream outputStream, InvocationEncoder dataEncoder, InvocationEncoder signatureEncoder, byte separator) throws Exception;

	InputStream wrap(Key publicKey, InputStream inputStream, InvocationEncoder dataEncoder, InvocationEncoder signatureEncoder, byte separator) throws Exception;

}
