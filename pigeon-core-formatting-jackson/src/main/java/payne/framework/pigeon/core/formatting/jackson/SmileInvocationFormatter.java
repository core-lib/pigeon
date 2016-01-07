package payne.framework.pigeon.core.formatting.jackson;

import payne.framework.pigeon.core.formatting.InvocationFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

public class SmileInvocationFormatter extends JacksonInvocationFormatter implements InvocationFormatter {

	public SmileInvocationFormatter() {
		super(new SmileFactory(), false);
	}

	public SmileInvocationFormatter(ObjectMapper mapper) {
		super(mapper, false);
	}

	public String algorithm() {
		return "application/smile";
	}

}
