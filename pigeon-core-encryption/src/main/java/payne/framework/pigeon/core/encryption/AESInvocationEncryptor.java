package payne.framework.pigeon.core.encryption;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import payne.framework.pigeon.core.encryption.exception.EncryptorException;
import payne.framework.pigeon.core.encryption.key.Key;
import payne.framework.pigeon.core.encryption.key.SymmetricSecureKey;
import payne.framework.pigeon.core.toolkit.IOToolkit;

/**
 * 密钥长度必须位 128 192 或 256 位,初始化向量长度16位
 * 
 * @author yangchangpei
 * 
 */
public class AESInvocationEncryptor extends DefaultInvocationEncryptor implements InvocationEncryptor {
	private static final String ALGORITHM = "AES/CBC/PKCS7Padding";
	private static final Random RANDOM = new Random();

	public AESInvocationEncryptor() {
		Security.addProvider(new BouncyCastleProvider());
	}

	public String algorithm() {
		return "AES";
	}

	public boolean symmetry() {
		return true;
	}

	public void encrypt(Key key, int keysize, InputStream in, OutputStream out) throws EncryptorException {
		CipherInputStream cis = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			byte[] kb = key.getEncryptKey().length <= keysize / 8 ? key.getEncryptKey() : Arrays.copyOf(key.getEncryptKey(), keysize / 8);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kb, ALGORITHM), new IvParameterSpec(key.getIv()));
			cis = new CipherInputStream(in, cipher);
			IOToolkit.transmit(cis, out);
		} catch (Exception e) {
			throw new EncryptorException(e);
		} finally {
			IOToolkit.close(cis);
		}
	}

	public void decrypt(Key key, int keysize, InputStream in, OutputStream out) throws EncryptorException {
		CipherOutputStream cos = null;
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			byte[] kb = key.getDecryptKey().length <= keysize / 8 ? key.getDecryptKey() : Arrays.copyOf(key.getDecryptKey(), keysize / 8);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kb, ALGORITHM), new IvParameterSpec(key.getIv()));
			cos = new CipherOutputStream(out, cipher);
			IOToolkit.transmit(in, cos);
		} catch (Exception e) {
			throw new EncryptorException(e);
		} finally {
			IOToolkit.close(cos);
		}
	}

	public Key generate(int keysize) throws EncryptorException {
		try {
			KeyGenerator generator = KeyGenerator.getInstance(algorithm());
			generator.init(keysize, new SecureRandom());
			SecretKey key = generator.generateKey();
			byte[] iv = new byte[16];
			for (int i = 0; i < iv.length; i++) {
				iv[i] = (byte) (RANDOM.nextInt(Byte.MAX_VALUE) + 1);
			}
			return new SymmetricSecureKey(algorithm(), keysize, key.getEncoded(), iv);
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptorException(e);
		}
	}

	public OutputStream wrap(Key key, int keysize, OutputStream outputStream) throws Exception {
		return new AESInvocationOutputStream(key, keysize, outputStream);
	}

	public InputStream wrap(Key key, int keysize, InputStream inputStream) throws Exception {
		return new AESInvocationInputStream(key, keysize, inputStream);
	}

}
