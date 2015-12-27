package payne.framework.pigeon.core.signature;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SHA1WithRSAInvocationSigner extends RSAInvocationSigner implements InvocationSigner {

	public SHA1WithRSAInvocationSigner() throws NoSuchAlgorithmException, IOException {
		super();
	}

	public String algorithm() {
		return "SHA1WithRSA";
	}

}
