package payne.framework.pigeon.server.session;

import java.util.Map;

public interface Session extends Map<String, Object> {

	String getId();

	long getLasttime();

}
