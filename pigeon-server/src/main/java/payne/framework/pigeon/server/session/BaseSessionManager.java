package payne.framework.pigeon.server.session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import payne.framework.pigeon.server.exception.IllegalOperationException;

public abstract class BaseSessionManager implements SessionManager {
	protected int expiration = 20 * 60 * 1000;
	protected int deviation = 10 * 1000;

	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	public int getDeviation() {
		return deviation;
	}

	public void setDeviation(int deviation) {
		this.deviation = deviation;
	}

	protected final Session create(String id) {
		return (Session) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { Session.class }, new SelfCheckingSession(id));
	}

	private static final class SelfCheckingSession extends HashMap<String, Object> implements Session, InvocationHandler {
		private static final long serialVersionUID = 4515998871118510729L;

		private final String id;
		private long lasttime = System.currentTimeMillis();

		public SelfCheckingSession(String id) {
			super();
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public long getLasttime() {
			return lasttime;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getId") || method.getName().equals("getLasttime")) {
				return method.invoke(this, args);
			}
			if (proxy != SessionLocal.getCurrent()) {
				throw new IllegalOperationException("session must be used in owner thread");
			}
			lasttime = System.currentTimeMillis();
			return method.invoke(this, args);
		}

	}

}
