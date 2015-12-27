package payne.framework.pigeon.client.cookie;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import payne.framework.pigeon.core.Cookie;
import payne.framework.pigeon.core.Task;
import payne.framework.pigeon.core.exception.TaskExecuteException;
import payne.framework.pigeon.core.exception.TaskRevokeException;

/**
 * 取长补短的cookie管理器,实现双方面管理,查找cookie先从瞬时的管理器中查询如果找不到则再从持久的管理器中查询并缓存到内存<br/>
 * 保存时先保存到内存再异步保存到持久的管理器
 * 
 * @author yangchangpei
 *
 */
public class ComplementaryCookieManager implements CookieManager, Runnable {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private BlockingQueue<CookiePersistenceTask> queue = new LinkedBlockingDeque<CookiePersistenceTask>();

	private CookieManager transientCookieManager;
	private CookieManager persistentCookieManager;

	{
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public ComplementaryCookieManager() {
		super();
		transientCookieManager = new InternalCookieManager();
		persistentCookieManager = new ExternalCookieManager();
	}

	public ComplementaryCookieManager(CookieManager transientCookieManager, CookieManager persistentCookieManager) {
		super();
		this.transientCookieManager = transientCookieManager;
		this.persistentCookieManager = persistentCookieManager;
	}

	public void save(Cookie... cookies) {
		transientCookieManager.save(cookies);
		queue.add(new CookiePersistenceTask(cookies));
	}

	public Cookie[] find(String domain, String path) {
		Cookie[] cookies = transientCookieManager.find(domain, path);
		if (cookies == null) {
			cookies = persistentCookieManager.find(domain, path);
			if (cookies != null) {
				transientCookieManager.save(cookies);
			}
		}
		return cookies;
	}

	public void run() {
		while (true) {
			try {
				CookiePersistenceTask task = queue.take();
				task.execute();
			} catch (Throwable e) {
				logger.error("cookie perstence fail", e);
			}
		}
	}

	public CookieManager getTransientCookieManager() {
		return transientCookieManager;
	}

	public void setTransientCookieManager(CookieManager transientCookieManager) {
		this.transientCookieManager = transientCookieManager;
	}

	public CookieManager getPersistentCookieManager() {
		return persistentCookieManager;
	}

	public void setPersistentCookieManager(CookieManager persistentCookieManager) {
		this.persistentCookieManager = persistentCookieManager;
	}

	private class CookiePersistenceTask implements Task {
		private Cookie[] cookies;

		public CookiePersistenceTask(Cookie[] cookies) {
			super();
			this.cookies = cookies;
		}

		public void execute() throws TaskExecuteException {
			persistentCookieManager.save(cookies);
		}

		public void revoke() throws TaskRevokeException {
			throw new UnsupportedOperationException("cookie persistence task can not be revoked");
		}

	}

}
