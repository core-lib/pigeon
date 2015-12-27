package payne.framework.pigeon.core.key;

public interface AsymmetricKey extends Key {

	byte[] getPublicKey();

	byte[] getPrivateKey();

}
