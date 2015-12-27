package payne.framework.pigeon.core.protocol;

import java.nio.charset.Charset;

public abstract class TranscoderChannel implements Channel {
	protected String charset = Charset.defaultCharset().name();

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
