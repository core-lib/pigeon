package payne.framework.pigeon.core.compression;

import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

public class FlatInvocationOutputStream extends DeflaterOutputStream {

	public FlatInvocationOutputStream(OutputStream out) {
		super(out);
	}

}
