package payne.framework.pigeon.core.encryption.key;

public final class AsymmetricSecureKey extends SecureKey implements AsymmetricKey {
	private static final long serialVersionUID = -5120495228878483696L;

	private final byte[] publicKey;
	private final byte[] privateKey;

	public AsymmetricSecureKey(String algorithm, int size, byte[] publicKey, byte[] privateKey) {
		super(algorithm, size);
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public byte[] getEncryptKey() {
		return publicKey;
	}

	public byte[] getDecryptKey() {
		return privateKey;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public byte[] getIv() {
		return null;
	}

}
