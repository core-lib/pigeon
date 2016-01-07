package payne.framework.pigeon.core.encryption.key;

public interface AsymmetricKey extends Key {

	byte[] getPublicKey();

	byte[] getPrivateKey();

}
