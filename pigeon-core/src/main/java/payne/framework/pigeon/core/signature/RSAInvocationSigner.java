package payne.framework.pigeon.core.signature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import payne.framework.pigeon.core.encoding.InvocationEncoder;
import payne.framework.pigeon.core.exception.SignerException;
import payne.framework.pigeon.core.key.Key;
import payne.framework.pigeon.core.toolkit.IOToolkit;
import payne.framework.pigeon.core.toolkit.InputStreamReadable;

public abstract class RSAInvocationSigner implements InvocationSigner {
	protected final KeyFactory keyFactory;

	public RSAInvocationSigner() throws NoSuchAlgorithmException, IOException {
		keyFactory = KeyFactory.getInstance("RSA");
		Security.addProvider(new BouncyCastleProvider());
	}

	public byte[] sign(Key key, byte[] bytes) throws SignerException {
		return sign(key, bytes, 0, bytes.length);
	}

	public byte[] sign(Key key, byte[] bytes, int offset, int length) throws SignerException {
		try {
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.getDecryptKey());
			PrivateKey privateKey = keyFactory.generatePrivate(spec);

			Signature signature = Signature.getInstance(algorithm());
			signature.initSign(privateKey);

			signature.update(bytes, offset, length);

			return signature.sign();
		} catch (Exception e) {
			throw new SignerException(e);
		}
	}

	public byte[] sign(Key key, InputStream in) throws SignerException {
		return sign(key, in, null);
	}

	public byte[] sign(Key key, InputStream in, OutputStream out) throws SignerException {
		try {
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.getDecryptKey());
			PrivateKey privateKey = keyFactory.generatePrivate(spec);

			Signature signature = Signature.getInstance(algorithm());
			signature.initSign(privateKey);

			IOToolkit.transmit(new InputStreamReadable(in), new MessageSignWritable(signature, out));
			return signature.sign();
		} catch (Exception e) {
			throw new SignerException(e);
		}
	}

	public boolean verify(Key key, byte[] signature, byte[] data) throws SignerException {
		return verify(key, signature, data, 0, data.length);
	}

	public boolean verify(Key key, byte[] signature, byte[] data, int offset, int length) throws SignerException {
		try {
			X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getEncryptKey());
			PublicKey publicKey = keyFactory.generatePublic(spec);

			Signature s = Signature.getInstance(algorithm());
			s.initVerify(publicKey);

			s.update(data, offset, length);

			return s.verify(signature);
		} catch (Exception e) {
			throw new SignerException(e);
		}
	}

	public boolean verify(Key key, byte[] signature, InputStream in) throws SignerException {
		return verify(key, signature, in, null);
	}

	public boolean verify(Key key, byte[] signature, InputStream in, OutputStream out) throws SignerException {
		try {
			X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getEncryptKey());
			PublicKey publicKey = keyFactory.generatePublic(spec);

			Signature s = Signature.getInstance(algorithm());
			s.initVerify(publicKey);

			IOToolkit.transmit(new InputStreamReadable(in), new MessageSignWritable(s, out));

			return s.verify(signature);
		} catch (Exception e) {
			throw new SignerException(e);
		}
	}

	public OutputStream wrap(Key privateKey, OutputStream outputStream, InvocationEncoder dataEncoder, InvocationEncoder signatureEncoder, byte separator) throws Exception {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey.getDecryptKey());
		PrivateKey key = keyFactory.generatePrivate(spec);
		Signature signature = Signature.getInstance(algorithm());
		signature.initSign(key);
		return new SignInvocationOutputStream(signature, outputStream, dataEncoder, signatureEncoder, separator);
	}

	public InputStream wrap(Key publicKey, InputStream inputStream, InvocationEncoder dataEncoder, InvocationEncoder signatureEncoder, byte separator) throws Exception {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey.getEncryptKey());
		PublicKey key = keyFactory.generatePublic(spec);
		Signature signature = Signature.getInstance(algorithm());
		signature.initVerify(key);
		return new SignInvocationInputStream(signature, inputStream, dataEncoder, signatureEncoder, separator);
	}

}
