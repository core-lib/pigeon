package payne.framework.pigeon.integration.web.spring;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Invocation;

public class OpenSessionInConnectedInterceptor implements Interceptor, ApplicationContextAware {
	private SessionFactory sessionFactory;

	public OpenSessionInConnectedInterceptor() {
		super();
	}

	public OpenSessionInConnectedInterceptor(SessionFactory sessionFactory) {
		super();
		this.sessionFactory = sessionFactory;
	}

	public Object intercept(Invocation invocation) throws Exception {
		try {
			Session session = sessionFactory.openSession();
			session.setFlushMode(FlushMode.MANUAL);
			SessionHolder sessionHolder = new SessionHolder(session);
			TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
			return invocation.invoke();
		} catch (Exception e) {
			throw e;
		} finally {
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			sessionHolder.getSession().close();
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		sessionFactory = applicationContext.getBean(SessionFactory.class);
	}

}
