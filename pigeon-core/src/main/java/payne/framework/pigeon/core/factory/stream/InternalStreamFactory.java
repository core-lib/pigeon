package payne.framework.pigeon.core.factory.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class InternalStreamFactory implements StreamFactory {

	public InputStream produce(OutputStream out) {
		return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
	}

	public OutputStream produce() {
		return new ByteArrayOutputStream();
	}

}
