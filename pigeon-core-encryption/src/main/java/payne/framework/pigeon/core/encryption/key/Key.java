package payne.framework.pigeon.core.encryption.key;

public interface Key {

	String getAlgorithm();

	int getSize();

	byte[] getEncryptKey();

	byte[] getDecryptKey();

	byte[] getIv();

}