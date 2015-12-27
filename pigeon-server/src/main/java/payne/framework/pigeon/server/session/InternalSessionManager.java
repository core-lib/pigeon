package payne.framework.pigeon.server.session;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalSessionManager extends BaseSessionManager implements SessionManager, Runnable {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	public InternalSessionManager() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public void bind(String id) {
		Session session = sessions.get(id);
		if (session == null) {
			synchronized (this) {
				if (!sessions.containsKey(id)) {
					session = create(id);
					sessions.put(id, session);
				} else {
					session = sessions.get(id);
				}
			}
		}
		SessionLocal.setCurrent(session);
	}

	public void unbind() {
		SessionLocal.setCurrent(null);
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(deviation);
				synchronized (this) {
					Iterator<Session> iterator = sessions.values().iterator();
					while (iterator.hasNext()) {
						Session session = iterator.next();
						if (System.currentTimeMillis() - session.getLasttime() > expiration) {
							iterator.remove();
						}
					}
				}
			} catch (Throwable e) {
				logger.error("scanning and removing expired sessions occur an exception", e);
			}
		}
	}

}
