package payne.framework.pigeon.core.compression;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPInvocationOutputStream extends GZIPOutputStream {

	public GZIPInvocationOutputStream(OutputStream out) throws IOException {
		super(out);
	}

}
