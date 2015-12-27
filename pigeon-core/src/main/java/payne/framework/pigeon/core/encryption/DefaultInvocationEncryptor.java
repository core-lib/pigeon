package payne.framework.pigeon.core.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import payne.framework.pigeon.core.exception.EncryptorException;
import payne.framework.pigeon.core.key.Key;

public abstract class DefaultInvocationEncryptor implements InvocationEncryptor {

	public byte[] encrypt(Key key, int keysize, byte[] bytes) throws EncryptorException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		encrypt(key, keysize, inputStream, outputStream);
		return outputStream.toByteArray();
	}

	public byte[] decrypt(Key key, int keysize, byte[] bytes) throws EncryptorException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		decrypt(key, keysize, inputStream, outputStream);
		return outputStream.toByteArray();
	}

}
