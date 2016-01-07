package payne.framework.pigeon.core.encoding;

import java.io.InputStream;

import org.apache.commons.codec.binary.Base64InputStream;

public class Base64InvocationInputStream extends Base64InputStream {

	public Base64InvocationInputStream(InputStream in) {
		super(in);
	}

}
