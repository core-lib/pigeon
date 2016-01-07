package payne.framework.pigeon.core.compression;

import java.io.OutputStream;

import org.xerial.snappy.SnappyOutputStream;

public class SnappyInvocationOutputStream extends SnappyOutputStream {

	public SnappyInvocationOutputStream(OutputStream out) {
		super(out);
	}

}
