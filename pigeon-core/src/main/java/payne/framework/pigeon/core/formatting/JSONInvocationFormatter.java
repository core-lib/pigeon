package payne.framework.pigeon.core.formatting;

import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json格式的调用对象格式化器
 * 
 * @author yangchangpei
 * 
 */
public class JSONInvocationFormatter extends JacksonInvocationFormatter implements InvocationFormatter {

	public JSONInvocationFormatter() {
		super(new MappingJsonFactory(), true);
	}

	public JSONInvocationFormatter(ObjectMapper mapper) {
		super(mapper, true);
	}

	public String algorithm() {
		return "application/json";
	}

}
