package payne.framework.pigeon.core.factory.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamFactory {

	InputStream produce(OutputStream out) throws IOException;

	OutputStream produce() throws IOException;

}
