package payne.framework.pigeon.client.cookie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import payne.framework.pigeon.core.Cookie;

/**
 * 内存cookie管理器,提供快速的cookie管理服务,但程序退出后cookie将消失
 * 
 * @author yangchangpei
 * 
 */
public class InternalCookieManager implements CookieManager {
	private Map<String, Set<Cookie>> map = new ConcurrentHashMap<String, Set<Cookie>>();

	public void save(Cookie... cookies) {
		for (Cookie cookie : cookies) {
			Set<Cookie> set = map.get(cookie.getDomain());
			synchronized (set != null ? set : map) {
				if (set == null) {
					set = new HashSet<Cookie>();
					map.put(cookie.getDomain(), set);
				}
				Iterator<Cookie> iterator = set.iterator();
				while (iterator.hasNext()) {
					Cookie c = iterator.next();
					if (c.getDomain().equals(cookie.getDomain()) || c.validate() == false) {
						iterator.remove();
						continue;
					}
				}
				if (cookie.validate() == true) {
					set.add(cookie);
				}
			}
		}
	}

	public Cookie[] find(String domain, String path) {
		Set<Cookie> set = map.get(domain);
		if (set == null) {
			return null;
		}
		List<Cookie> list = new ArrayList<Cookie>();
		synchronized (set) {
			Iterator<Cookie> iterator = set.iterator();
			while (iterator.hasNext()) {
				Cookie c = iterator.next();
				if (c.validate() == false) {
					iterator.remove();
					continue;
				}
				if (path.startsWith(c.getPath())) {
					list.add(c);
				}
			}
		}
		return list.toArray(new Cookie[list.size()]);
	}

}
