package payne.framework.pigeon.core.toolkit;

import java.io.IOException;

public interface Writable {

	int write(int b) throws IOException;

	int write(byte[] b) throws IOException;

	int write(byte[] b, int off, int len) throws IOException;

}
