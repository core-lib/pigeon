package payne.framework.pigeon.core.factory.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.qfox.detector.DefaultResourceDetector;
import org.qfox.detector.Resource;
import org.qfox.detector.ResourceDetector;
import org.qfox.detector.ResourceFilter;
import org.qfox.detector.ResourceFilterChain;

import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.exception.InexistentBeanException;

public abstract class ConfigurableBeanFactory implements BeanFactory, ResourceFilter {
	protected final ClassLoader classLoader;
	protected final List<String> configLocations = new ArrayList<String>();
	protected final List<Configuration> configurations = new ArrayList<Configuration>();

	protected ConfigurableBeanFactory() {
		this(Thread.currentThread().getContextClassLoader());
	}

	protected ConfigurableBeanFactory(String... configLocations) {
		this(Thread.currentThread().getContextClassLoader(), configLocations);
	}

	protected ConfigurableBeanFactory(Properties properties) {
		this(properties, Thread.currentThread().getContextClassLoader());
	}

	protected ConfigurableBeanFactory(ClassLoader classLoader) {
		this.classLoader = classLoader;
		try {
			ResourceDetector detector = DefaultResourceDetector.Builder.scan("pigeon").by(classLoader).build();
			Collection<Resource> configs = detector.detect(this);
			for (Resource config : configs) {
				Configuration configuration = new Configuration(config);
				this.configLocations.add(config.getName());
				this.configurations.add(configuration);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected ConfigurableBeanFactory(ClassLoader classLoader, String... configLocations) {
		this.classLoader = classLoader;
		try {
			for (String location : configLocations) {
				ResourceDetector detector = DefaultResourceDetector.Builder.scan(location).by(classLoader).build();
				Collection<Resource> configs = detector.detect(this);
				for (Resource config : configs) {
					Configuration configuration = new Configuration(config);
					this.configLocations.add(config.getName());
					this.configurations.add(configuration);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected ConfigurableBeanFactory(Properties properties, ClassLoader classLoader) {
		this.classLoader = classLoader;
		Configuration configuration = new Configuration(null, properties);
		this.configurations.add(configuration);
	}

	public boolean accept(Resource resource, ResourceFilterChain chain) {
		return resource.getName().endsWith(".properties") ? chain.doNext(resource) : false;
	}

	public boolean contains(String name) {
		for (Configuration configuration : configurations) {
			if (configuration.properties.containsKey(name)) {
				return true;
			}
		}
		return false;
	}

	public <T> T get(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		return establish(type);
	}

	public <T> T establish(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		try {
			return type.newInstance();
		} catch (Throwable e) {
			throw new BeanInitializeException(e, type, null);
		}
	}

	public String value(String name) {
		for (Configuration configuration : configurations) {
			if (configuration.properties.containsKey(name)) {
				return configuration.properties.getProperty(name);
			}
		}
		return null;
	}

	public String value(String name, String defaultValue) {
		for (Configuration configuration : configurations) {
			if (configuration.properties.containsKey(name)) {
				return configuration.properties.getProperty(name);
			}
		}
		return defaultValue;
	}

	public <T> T get(String name, Class<T> type) throws InexistentBeanException, BeanInitializeException, ClassNotFoundException {
		if (this.contains(name) == false) {
			throw new InexistentBeanException("property of name " + name + " is not defined", null, name);
		}
		String property = this.value(name);
		try {
			Class<?> clazz = classLoader.loadClass(property);
			return type.cast(get(clazz));
		} catch (Throwable e) {
			throw new InexistentBeanException(e, type, name);
		}
	}

	public <T> T establish(String name, Class<T> type) throws InexistentBeanException, BeanInitializeException, ClassNotFoundException {
		if (this.contains(name) == false) {
			throw new InexistentBeanException("property of name " + name + " is not defined", null, name);
		}
		String property = this.value(name);
		try {
			Class<?> clazz = classLoader.loadClass(property);
			return type.cast(establish(clazz));
		} catch (Throwable e) {
			throw new InexistentBeanException(type, name);
		}
	}

	public <T> Map<String, T> find(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		Map<String, T> map = new HashMap<String, T>();
		for (Configuration configuration : configurations) {
			for (Entry<Object, Object> entry : configuration.properties.entrySet()) {
				Class<?> clazz = null;
				try {
					clazz = classLoader.loadClass(entry.getValue().toString());
				} catch (Throwable e) {
					continue;
				}
				if (type.isAssignableFrom(clazz)) {
					map.put(entry.getKey().toString(), type.cast(get(clazz)));
				}
			}
		}
		return map;
	}

}
