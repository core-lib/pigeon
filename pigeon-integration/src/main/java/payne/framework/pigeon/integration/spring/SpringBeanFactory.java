package payne.framework.pigeon.integration.spring;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.exception.InexistentBeanException;
import payne.framework.pigeon.core.factory.bean.BeanFactory;
import payne.framework.pigeon.core.factory.bean.ConfigurationBeanFactory;
import payne.framework.pigeon.core.factory.bean.SingletonBeanFactory;

public class SpringBeanFactory implements BeanFactory, ApplicationContextAware {
	private ApplicationContext applicationContext;

	private String[] beanConfigurationPaths;
	private ConfigurationBeanFactory configurationBeanFactory;

	public SpringBeanFactory() throws IOException {
		this("pigeon.properties");
	}

	public SpringBeanFactory(String... beanConfigurationPaths) {
		this.beanConfigurationPaths = beanConfigurationPaths;
	}

	public SpringBeanFactory(ApplicationContext applicationContext) {
		this.setApplicationContext(applicationContext);
	}

	public SpringBeanFactory(ApplicationContext applicationContext, String... beanConfigurationPaths) {
		this(beanConfigurationPaths);
		this.setApplicationContext(applicationContext);
	}

	public boolean contains(String name) {
		return applicationContext.containsBean(name) || configurationBeanFactory.contains(name);
	}

	public <T> T get(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		try {
			return applicationContext.getBean(type);
		} catch (BeansException e) {
			return configurationBeanFactory.get(type);
		}
	}

	public <T> T establish(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		return configurationBeanFactory.establish(type);
	}

	public String value(String name) {
		return configurationBeanFactory.value(name);
	}

	public String value(String name, String _default) {
		return configurationBeanFactory.value(name, _default);
	}

	public <T> T get(String name, Class<T> type) throws InexistentBeanException {
		try {
			if (applicationContext.containsBean(name)) {
				return type.cast(applicationContext.getBean(name));
			} else {
				return configurationBeanFactory.get(name, type);
			}
		} catch (Exception e) {
			throw new InexistentBeanException(e, type, name);
		}
	}

	public <T> T establish(String name, Class<T> type) throws InexistentBeanException, BeanInitializeException, ClassNotFoundException {
		return configurationBeanFactory.establish(name, type);
	}

	public <T> Map<String, T> find(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		Map<String, T> map = applicationContext.getBeansOfType(type, true, true);
		Map<String, T> m = configurationBeanFactory.find(type);
		map.putAll(m);
		return map;
	}

	public String[] getBeanConfigurationPaths() {
		return beanConfigurationPaths;
	}

	public void setBeanConfigurationPath(String[] beanConfigurationPaths) {
		this.beanConfigurationPaths = beanConfigurationPaths;
	}

	public ConfigurationBeanFactory getConfigurationBeanFactory() {
		return configurationBeanFactory;
	}

	public void setConfigurationBeanFactory(ConfigurationBeanFactory configurationBeanFactory) {
		this.configurationBeanFactory = configurationBeanFactory;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		try {
			configurationBeanFactory = configurationBeanFactory != null ? configurationBeanFactory : new SingletonBeanFactory(applicationContext.getClassLoader(), beanConfigurationPaths);
		} catch (IOException e) {
			throw new BeanCreationException("count not initialize SpringBeanFactory with properties path : " + Arrays.toString(beanConfigurationPaths), e);
		}
	}

}
