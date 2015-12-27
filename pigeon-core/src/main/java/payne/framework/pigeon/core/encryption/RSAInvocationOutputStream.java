package payne.framework.pigeon.core.encryption;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import payne.framework.pigeon.core.key.Key;

public class RSAInvocationOutputStream extends OutputStream {
	private static final String ALGORITHM = "RSA/NONE/NoPadding";
	private final KeyFactory keyFactory;

	private final OutputStream out;
	private final Cipher cipher;

	private final byte[] buffer;
	private int position;

	public RSAInvocationOutputStream(Key key, int keysize, OutputStream out) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
		keyFactory = KeyFactory.getInstance("RSA");
		Security.addProvider(new BouncyCastleProvider());

		X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getEncryptKey());
		PublicKey publicKey = keyFactory.generatePublic(spec);

		cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		cipher.init(Cipher.PUBLIC_KEY, publicKey);

		this.out = out;

		buffer = new byte[keysize / 8 - 11];
		position = 0;
	}

	@Override
	public void write(int b) throws IOException {
		if (position == buffer.length) {
			try {
				out.write(cipher.doFinal(buffer, 0, position));
			} catch (Exception e) {
				throw new IOException(e);
			}
			position = 0;
		}
		buffer[position++] = (byte) b;
	}

	@Override
	public void flush() throws IOException {
		if (position > 0) {
			try {
				out.write(cipher.doFinal(buffer, 0, position));
			} catch (Exception e) {
				throw new IOException(e);
			}
			position = 0;
		}
		out.flush();
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
