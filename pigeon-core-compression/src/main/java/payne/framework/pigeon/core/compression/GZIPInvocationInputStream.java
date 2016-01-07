package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class GZIPInvocationInputStream extends GZIPInputStream {

	public GZIPInvocationInputStream(InputStream in) throws IOException {
		super(in);
	}

}
