package payne.framework.pigeon.core.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import payne.framework.pigeon.core.encryption.key.Key;

public class RSAInvocationInputStream extends InputStream {
	private static final String ALGORITHM = "RSA/NONE/NoPadding";
	private final KeyFactory keyFactory;

	private final InputStream in;
	private final Cipher cipher;

	private final byte[] encrypted;
	private byte[] decrypted;
	private int position;

	public RSAInvocationInputStream(Key key, int keysize, InputStream in) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
		keyFactory = KeyFactory.getInstance("RSA");
		Security.addProvider(new BouncyCastleProvider());

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.getDecryptKey());
		PrivateKey privateKey = keyFactory.generatePrivate(spec);

		cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		cipher.init(Cipher.PRIVATE_KEY, privateKey);

		this.in = in;

		encrypted = new byte[keysize / 8];
		decrypted = new byte[0];
		position = 0;
	}

	@Override
	public int read() throws IOException {
		if (position == decrypted.length) {
			int length = in.read(encrypted);
			if (length == -1) {
				return -1;
			}
			try {
				decrypted = cipher.doFinal(encrypted, 0, length);
			} catch (Exception e) {
				throw new IOException(e);
			}
			position = 0;
		}
		return decrypted[position++] & 0xff;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
