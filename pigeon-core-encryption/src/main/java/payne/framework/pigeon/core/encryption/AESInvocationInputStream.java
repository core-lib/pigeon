package payne.framework.pigeon.core.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import payne.framework.pigeon.core.encryption.key.Key;

public class AESInvocationInputStream extends InputStream {
	private static final String ALGORITHM = "AES/CBC/PKCS7Padding";

	private final CipherInputStream in;

	public AESInvocationInputStream(Key key, int keysize, InputStream in) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		byte[] kb = key.getDecryptKey().length <= keysize / 8 ? key.getDecryptKey() : Arrays.copyOf(key.getDecryptKey(), keysize / 8);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kb, ALGORITHM), new IvParameterSpec(key.getIv()));
		this.in = new CipherInputStream(in, cipher);
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

}
