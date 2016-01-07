package payne.framework.pigeon.core.encryption;

import java.io.InputStream;
import java.io.OutputStream;

import payne.framework.pigeon.core.Conversion;
import payne.framework.pigeon.core.encryption.exception.EncryptorException;
import payne.framework.pigeon.core.encryption.key.Key;

public interface InvocationEncryptor extends Conversion {

	boolean symmetry();

	void encrypt(Key key, int keysize, InputStream in, OutputStream out) throws EncryptorException;

	void decrypt(Key key, int keysize, InputStream in, OutputStream out) throws EncryptorException;

	byte[] encrypt(Key key, int keysize, byte[] bytes) throws EncryptorException;

	byte[] decrypt(Key key, int keysize, byte[] bytes) throws EncryptorException;

	Key generate(int keysize) throws EncryptorException;

	OutputStream wrap(Key key, int keysize, OutputStream outputStream) throws Exception;

	InputStream wrap(Key key, int keysize, InputStream inputStream) throws Exception;

}
