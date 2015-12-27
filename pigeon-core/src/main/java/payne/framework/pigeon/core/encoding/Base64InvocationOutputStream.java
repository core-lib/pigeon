package payne.framework.pigeon.core.encoding;

import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64OutputStream;

public class Base64InvocationOutputStream extends Base64OutputStream {

	public Base64InvocationOutputStream(OutputStream out) {
		super(out);
	}

}
