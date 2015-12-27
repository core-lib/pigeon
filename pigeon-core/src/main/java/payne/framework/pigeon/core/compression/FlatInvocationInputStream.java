package payne.framework.pigeon.core.compression;

import java.io.InputStream;
import java.util.zip.InflaterInputStream;

public class FlatInvocationInputStream extends InflaterInputStream {

	public FlatInvocationInputStream(InputStream in) {
		super(in);
	}

}
