package payne.framework.pigeon.core.toolkit;

import java.io.IOException;
import java.io.OutputStream;

public abstract class InvocationOutputStream extends OutputStream {

	public void write(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		for (int i = 0; i < len; i++) {
			write(b[off + i]);
		}
	}

}
