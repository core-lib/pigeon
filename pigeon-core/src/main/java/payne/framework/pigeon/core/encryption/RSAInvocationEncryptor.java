package payne.framework.pigeon.core.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import payne.framework.pigeon.core.exception.EncryptorException;
import payne.framework.pigeon.core.key.AsymmetricSecureKey;
import payne.framework.pigeon.core.key.Key;

public class RSAInvocationEncryptor extends DefaultInvocationEncryptor implements InvocationEncryptor {
	private static final String ALGORITHM = "RSA/NONE/NoPadding";
	private final KeyFactory keyFactory;

	public RSAInvocationEncryptor() throws NoSuchAlgorithmException, IOException {
		keyFactory = KeyFactory.getInstance(algorithm());
		Security.addProvider(new BouncyCastleProvider());
	}

	public String algorithm() {
		return "RSA";
	}

	public boolean symmetry() {
		return false;
	}

	public void encrypt(Key key, int keysize, InputStream in, OutputStream out) throws EncryptorException {
		try {
			X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getEncryptKey());
			PublicKey publicKey = keyFactory.generatePublic(spec);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			cipher.init(Cipher.PUBLIC_KEY, publicKey);
			int length = 0;
			byte[] segment = new byte[keysize / 8 - 11];
			while ((length = in.read(segment)) != -1) {
				out.write(cipher.doFinal(segment, 0, length));
			}
		} catch (Exception e) {
			throw new EncryptorException(e);
		}
	}

	public void decrypt(Key key, int keysize, InputStream in, OutputStream out) throws EncryptorException {
		try {
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.getDecryptKey());
			PrivateKey privateKey = keyFactory.generatePrivate(spec);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			cipher.init(Cipher.PRIVATE_KEY, privateKey);
			int length = 0;
			byte[] segment = new byte[keysize / 8];
			while ((length = in.read(segment)) != -1) {
				out.write(cipher.doFinal(segment, 0, length));
			}
		} catch (Exception e) {
			throw new EncryptorException(e);
		}
	}

	public Key generate(int keysize) throws EncryptorException {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(algorithm());
			kpg.initialize(keysize, new SecureRandom());
			KeyPair pair = kpg.generateKeyPair();
			return new AsymmetricSecureKey(algorithm(), keysize, pair.getPublic().getEncoded(), pair.getPrivate().getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new EncryptorException(e);
		}
	}

	public OutputStream wrap(Key key, int keysize, OutputStream outputStream) throws Exception {
		return new RSAInvocationOutputStream(key, keysize, outputStream);
	}

	public InputStream wrap(Key key, int keysize, InputStream inputStream) throws Exception {
		return new RSAInvocationInputStream(key, keysize, inputStream);
	}

}
