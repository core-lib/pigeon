package payne.framework.pigeon.core.key;

public interface Key {

	String getAlgorithm();

	int getSize();

	byte[] getEncryptKey();

	byte[] getDecryptKey();

	byte[] getIv();

}