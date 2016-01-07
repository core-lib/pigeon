package payne.framework.pigeon.core.formatting.jackson;

import payne.framework.pigeon.core.formatting.InvocationFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YAMLInvocationFormatter extends JacksonInvocationFormatter implements InvocationFormatter {

	public YAMLInvocationFormatter() {
		super(new YAMLFactory(), true);
	}

	public YAMLInvocationFormatter(ObjectMapper mapper) {
		super(mapper, true);
	}

	public String algorithm() {
		return "application/yaml";
	}

}
