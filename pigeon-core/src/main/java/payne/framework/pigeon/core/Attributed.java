package payne.framework.pigeon.core;

import java.util.Map;

public interface Attributed {

	Object addAttribute(String key, Object value);

	Object getAttribute(String key);

	Object removeAttribute(String key);

	Map<String, Object> getAttributes();

}
