package payne.framework.pigeon.core.protocol;

import java.util.HashMap;
import java.util.Map;

public abstract class AttributedChannel extends TranscoderChannel {
	protected Map<String, Object> attributes = new HashMap<String, Object>();

	public Object addAttribute(String key, Object value) {
		return attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

}
