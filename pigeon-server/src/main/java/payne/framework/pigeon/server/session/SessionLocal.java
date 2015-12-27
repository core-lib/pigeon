package payne.framework.pigeon.server.session;

public final class SessionLocal {
	private static final ThreadLocal<Session> SESSION = new ThreadLocal<Session>();

	public static Session getCurrent() {
		return SESSION.get();
	}

	static void setCurrent(Session session) {
		if (session != null) {
			SESSION.set(session);
		} else {
			SESSION.remove();
		}
	}

}
