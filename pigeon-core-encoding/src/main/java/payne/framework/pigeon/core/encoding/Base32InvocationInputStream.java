package payne.framework.pigeon.core.encoding;

import java.io.InputStream;

import org.apache.commons.codec.binary.Base32InputStream;

public class Base32InvocationInputStream extends Base32InputStream {

	public Base32InvocationInputStream(InputStream in) {
		super(in);
	}

}
