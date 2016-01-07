package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.InputStream;

import org.xerial.snappy.SnappyInputStream;

public class SnappyInvocationInputStream extends SnappyInputStream {

	public SnappyInvocationInputStream(InputStream in) throws IOException {
		super(in);
	}

}
