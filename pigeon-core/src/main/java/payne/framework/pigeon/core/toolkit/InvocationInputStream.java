package payne.framework.pigeon.core.toolkit;

import java.io.IOException;
import java.io.InputStream;

public abstract class InvocationInputStream extends InputStream {

	public int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}
		try {
			int i, c;
			for (i = 0; i < len; i++) {
				c = read();
				if (c == -1) {
					return i == 0 ? -1 : i;
				}
				b[off + i] = (byte) c;
			}
			return i;
		} catch (IOException e) {
			throw e;
		}
	}

}
