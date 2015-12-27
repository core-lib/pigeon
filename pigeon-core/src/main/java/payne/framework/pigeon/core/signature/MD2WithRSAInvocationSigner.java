package payne.framework.pigeon.core.signature;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MD2WithRSAInvocationSigner extends RSAInvocationSigner implements InvocationSigner {

	public MD2WithRSAInvocationSigner() throws NoSuchAlgorithmException, IOException {
		super();
	}

	public String algorithm() {
		return "MD2WithRSA";
	}

}
