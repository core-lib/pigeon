package payne.framework.pigeon.client.cookie;

import payne.framework.pigeon.core.Cookie;

/**
 * cookie 管理器,提供cookie查询和cookie保存功能,domain path 和
 * name相同的cookie将会进行覆盖,过期的cookie将会自动忽略
 * 
 * @author yangchangpei
 *
 */
public interface CookieManager {
	
	/**
	 * 保存cookie
	 * 
	 * @param cookies
	 */
	void save(Cookie... cookies);

	/**
	 * 根据cookie的domain和path寻找对应的cookie
	 * 
	 * @param domain
	 * @param path
	 * @return
	 */
	Cookie[] find(String domain, String path);

}
