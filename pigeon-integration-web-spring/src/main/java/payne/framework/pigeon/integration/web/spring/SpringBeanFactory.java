package payne.framework.pigeon.integration.web.spring;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.exception.InexistentBeanException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.bean.ConfigurableBeanFactory;
import payne.framework.pigeon.core.factory.bean.SingletonBeanFactory;

public class SpringBeanFactory implements BeanFactory, ApplicationContextAware {
	private ApplicationContext applicationContext;

	private String[] configLocations;
	private ConfigurableBeanFactory configurableBeanFactory;

	public SpringBeanFactory() {
		this("pigeon");
	}

	public SpringBeanFactory(String... configLocations) {
		this.configLocations = configLocations;
	}

	public SpringBeanFactory(ApplicationContext applicationContext) {
		this.setApplicationContext(applicationContext);
	}

	public SpringBeanFactory(ApplicationContext applicationContext, String... configLocations) {
		this(configLocations);
		this.setApplicationContext(applicationContext);
	}

	public boolean contains(String name) {
		return applicationContext.containsBean(name) || configurableBeanFactory.contains(name);
	}

	public <T> T get(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		try {
			return applicationContext.getBean(type);
		} catch (BeansException e) {
			return configurableBeanFactory.get(type);
		}
	}

	public <T> T establish(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		return configurableBeanFactory.establish(type);
	}

	public String value(String name) {
		return configurableBeanFactory.value(name);
	}

	public String value(String name, String defaultValue) {
		return configurableBeanFactory.value(name, defaultValue);
	}

	public <T> T get(String name, Class<T> type) throws InexistentBeanException {
		try {
			if (applicationContext.containsBean(name)) {
				return type.cast(applicationContext.getBean(name));
			} else {
				return configurableBeanFactory.get(name, type);
			}
		} catch (Exception e) {
			throw new InexistentBeanException(e, type, name);
		}
	}

	public <T> T establish(String name, Class<T> type) throws InexistentBeanException, BeanInitializeException, ClassNotFoundException {
		return configurableBeanFactory.establish(name, type);
	}

	public <T> Map<String, T> find(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		Map<String, T> map = applicationContext.getBeansOfType(type, true, true);
		Map<String, T> m = configurableBeanFactory.find(type);
		map.putAll(m);
		return map;
	}

	public String[] getConfigLocations() {
		return configLocations;
	}

	public void setConfigLocations(String[] configLocations) {
		this.configLocations = configLocations;
	}

	public ConfigurableBeanFactory getConfigurableBeanFactory() {
		return configurableBeanFactory;
	}

	public void setConfigurableBeanFactory(ConfigurableBeanFactory configurableBeanFactory) {
		this.configurableBeanFactory = configurableBeanFactory;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		this.configurableBeanFactory = configurableBeanFactory != null ? configurableBeanFactory : new SingletonBeanFactory(applicationContext.getClassLoader(), configLocations);
	}

}
