package payne.framework.pigeon.core.encryption;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import payne.framework.pigeon.core.encryption.key.Key;

public class DESInvocationOutputStream extends OutputStream {
	private static final String ALGORITHM = "DES/CBC/PKCS7Padding";

	private final CipherOutputStream out;

	public DESInvocationOutputStream(Key key, int keysize, OutputStream out) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		byte[] kb = key.getDecryptKey().length <= keysize / 7 ? key.getDecryptKey() : Arrays.copyOf(key.getDecryptKey(), keysize / 7);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kb, ALGORITHM), new IvParameterSpec(key.getIv()));
		this.out = new CipherOutputStream(out, cipher);
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

}
