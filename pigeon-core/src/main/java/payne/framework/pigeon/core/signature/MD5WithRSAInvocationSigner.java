package payne.framework.pigeon.core.signature;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MD5WithRSAInvocationSigner extends RSAInvocationSigner implements InvocationSigner {

	public MD5WithRSAInvocationSigner() throws NoSuchAlgorithmException, IOException {
		super();
	}

	public String algorithm() {
		return "MD5WithRSA";
	}

}
