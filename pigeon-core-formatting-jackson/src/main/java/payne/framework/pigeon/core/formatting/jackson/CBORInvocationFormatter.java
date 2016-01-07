package payne.framework.pigeon.core.formatting.jackson;

import payne.framework.pigeon.core.formatting.InvocationFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

public class CBORInvocationFormatter extends JacksonInvocationFormatter implements InvocationFormatter {

	public CBORInvocationFormatter() {
		super(new CBORFactory(), false);
	}

	public CBORInvocationFormatter(ObjectMapper mapper) {
		super(mapper, false);
	}

	public String algorithm() {
		return "application/cbor";
	}

}
