package payne.framework.pigeon.core.encryption;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SHA256WithRSAInvocationSigner extends RSAInvocationSigner implements InvocationSigner {

	public SHA256WithRSAInvocationSigner() throws NoSuchAlgorithmException, IOException {
		super();
	}

	public String algorithm() {
		return "SHA256WithRSA";
	}

}
