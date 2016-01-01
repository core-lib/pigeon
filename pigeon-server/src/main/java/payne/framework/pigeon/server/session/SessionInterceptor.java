package payne.framework.pigeon.server.session;

import java.util.UUID;

import payne.framework.pigeon.core.Constants;
import payne.framework.pigeon.core.Cookie;
import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Invocation;

public class SessionInterceptor implements Interceptor, Constants {
	private final String name = "JSESSIONID";
	private SessionManager sessionManager = new InternalSessionManager();

	public Object intercept(Invocation invocation) throws Exception {
		try {
			Cookie cookie = Cookie.getRequestCookie(invocation.getClientHeader(), name);
			if (cookie == null) {
				cookie = new Cookie(name, UUID.randomUUID().toString());
				cookie.setMaxage(7 * 24 * 60 * 60);
				cookie.setPath("/");
				Cookie.addResponseCookie(invocation.getServerHeader(), cookie);
			}
			String id = cookie.getValue();
			sessionManager.bind(id);
			return invocation.invoke();
		} finally {
			sessionManager.unbind();
		}
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

}
