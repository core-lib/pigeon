package payne.framework.pigeon.integration.web.spring;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import payne.framework.pigeon.core.Interceptor;
import payne.framework.pigeon.core.Invocation;

public class OpenEntityManagerInConnectedInterceptor implements Interceptor, ApplicationContextAware {
	private EntityManagerFactory entityManagerFactory;

	public OpenEntityManagerInConnectedInterceptor() {
		super();
	}

	public OpenEntityManagerInConnectedInterceptor(EntityManagerFactory entityManagerFactory) {
		super();
		this.entityManagerFactory = entityManagerFactory;
	}

	public Object intercept(Invocation invocation) throws Exception {
		try {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityManagerHolder entityManagerHolder = new EntityManagerHolder(entityManager);
			TransactionSynchronizationManager.bindResource(entityManagerFactory, entityManagerHolder);
			return invocation.invoke();
		} catch (Exception e) {
			throw e;
		} finally {
			EntityManagerHolder entityManagerHolder = (EntityManagerHolder) TransactionSynchronizationManager.unbindResource(entityManagerFactory);
			entityManagerHolder.getEntityManager().close();
		}
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);
	}

}
