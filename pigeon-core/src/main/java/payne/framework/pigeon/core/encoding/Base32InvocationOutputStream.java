package payne.framework.pigeon.core.encoding;

import java.io.OutputStream;

import org.apache.commons.codec.binary.Base32OutputStream;

public class Base32InvocationOutputStream extends Base32OutputStream {

	public Base32InvocationOutputStream(OutputStream out) {
		super(out);
	}

}
