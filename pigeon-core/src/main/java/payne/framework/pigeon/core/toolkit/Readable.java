package payne.framework.pigeon.core.toolkit;

import java.io.IOException;

public interface Readable {
	
	int read() throws IOException;

	int read(byte[] b) throws IOException;

	int read(byte[] b, int off, int len) throws IOException;

}
