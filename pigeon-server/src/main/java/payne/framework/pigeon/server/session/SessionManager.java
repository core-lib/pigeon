package payne.framework.pigeon.server.session;

public interface SessionManager {

	void bind(String id);

	void unbind();

	int getExpiration();

	void setExpiration(int expiration);

	int getDeviation();

	void setDeviation(int deviation);

}
