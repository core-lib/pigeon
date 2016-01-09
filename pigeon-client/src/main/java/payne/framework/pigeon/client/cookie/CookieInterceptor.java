package payne.framework.pigeon.client.cookie;

import payne.framework.pigeon.core.Cookie;
import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Invocation;

public class CookieInterceptor implements Interceptor {
	private final CookieManager cookieManager;

	public CookieInterceptor(CookieManager cookieManager) {
		super();
		this.cookieManager = cookieManager;
	}

	public Object intercept(Invocation invocation) throws Exception {
		String domain = invocation.getHost();
		String path = invocation.getFile();
		try {
			Cookie[] cookies = cookieManager.find(domain, path);
			Cookie.addRequestCookies(invocation.getClientHeader(), cookies != null ? cookies : new Cookie[0]);
			return invocation.invoke();
		} finally {
			Cookie[] cookies = invocation.getServerHeader() == null ? new Cookie[0] : Cookie.getResponseCookies(invocation.getServerHeader());
			for (Cookie cookie : cookies) {
				cookie.setDomain(cookie.getDomain() != null ? cookie.getDomain() : domain);
				cookie.setPath(cookie.getPath() != null ? cookie.getPath() : path);
			}
			cookieManager.save(cookies != null ? cookies : new Cookie[0]);
		}
	}

	public CookieManager getCookieManager() {
		return cookieManager;
	}

}
