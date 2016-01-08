package payne.framework.pigeon.core.factory.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.exception.InexistentBeanException;

public class SingletonBeanFactory extends ConfigurableBeanFactory implements BeanFactory {
	private Map<Class<?>, Object> cache = new HashMap<Class<?>, Object>();

	public SingletonBeanFactory() {
		super();
	}

	public SingletonBeanFactory(ClassLoader classLoader, String... configLocations) {
		super(classLoader, configLocations);
	}

	public SingletonBeanFactory(ClassLoader classLoader) {
		super(classLoader);
	}

	public SingletonBeanFactory(Properties properties, ClassLoader classLoader) {
		super(properties, classLoader);
	}

	public SingletonBeanFactory(Properties properties) {
		super(properties);
	}

	public SingletonBeanFactory(String... configLocations) {
		super(configLocations);
	}

	@Override
	public <T> T get(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		if (!cache.containsKey(type)) {
			synchronized (type) {
				if (!cache.containsKey(type)) {
					cache.put(type, super.get(type));
				}
			}
		}
		return type.cast(cache.get(type));
	}

}
