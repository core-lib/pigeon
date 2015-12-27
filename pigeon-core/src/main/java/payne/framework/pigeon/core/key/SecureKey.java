package payne.framework.pigeon.core.key;

import java.io.Serializable;

public abstract class SecureKey implements Key, Serializable {
	private static final long serialVersionUID = -5577962754674149355L;

	protected final String algorithm;
	protected final int size;

	public SecureKey(String algorithm, int size) {
		super();
		this.algorithm = algorithm;
		this.size = size;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public int getSize() {
		return size;
	}

}
