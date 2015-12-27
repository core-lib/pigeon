package payne.framework.pigeon.core.factory.bean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.exception.InexistentBeanException;

public abstract class ConfigurationBeanFactory implements BeanFactory {
	protected String[] paths;
	protected ClassLoader classLoader;
	protected Properties properties;

	protected ConfigurationBeanFactory() throws IOException {
		this(Thread.currentThread().getContextClassLoader());
	}

	protected ConfigurationBeanFactory(String... paths) throws IOException {
		this(Thread.currentThread().getContextClassLoader(), paths);
	}

	protected ConfigurationBeanFactory(Properties properties) {
		this(properties, Thread.currentThread().getContextClassLoader());
	}

	protected ConfigurationBeanFactory(ClassLoader classLoader) throws IOException {
		this(classLoader, "pigeon.properties");
	}

	protected ConfigurationBeanFactory(ClassLoader classLoader, String... paths) throws IOException {
		this.classLoader = classLoader;
		this.paths = paths;
		this.properties = new Properties();
		for (String path : paths) {
			InputStream in = classLoader.getResourceAsStream(path);
			if (in == null) {
				throw new FileNotFoundException("properties file not found in classpath " + path);
			}
			Properties temp = new Properties();
			temp.load(in);
			this.properties.putAll(temp);
		}
	}

	protected ConfigurationBeanFactory(Properties properties, ClassLoader classLoader) {
		this.properties = new Properties();
		this.properties.putAll(properties);
		this.classLoader = classLoader;
	}

	public boolean contains(String name) {
		return properties.containsKey(name);
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
		return properties.getProperty(name);
	}

	public String value(String name, String _default) {
		return properties.contains(name) ? properties.getProperty(name) : _default;
	}

	public <T> T get(String name, Class<T> type) throws InexistentBeanException, BeanInitializeException, ClassNotFoundException {
		if (!properties.containsKey(name)) {
			throw new InexistentBeanException("property of name " + name + " is not defined", null, name);
		}
		String property = properties.getProperty(name);
		try {
			Class<?> clazz = classLoader.loadClass(property);
			return type.cast(get(clazz));
		} catch (Throwable e) {
			throw new InexistentBeanException(e, type, name);
		}
	}

	public <T> T establish(String name, Class<T> type) throws InexistentBeanException, BeanInitializeException, ClassNotFoundException {
		if (!properties.containsKey(name)) {
			throw new InexistentBeanException("property of name " + name + " is not defined", null, name);
		}
		String property = properties.getProperty(name);
		try {
			Class<?> clazz = classLoader.loadClass(property);
			return type.cast(establish(clazz));
		} catch (Throwable e) {
			throw new InexistentBeanException(type, name);
		}
	}

	public <T> Map<String, T> find(Class<T> type) throws InexistentBeanException, BeanInitializeException {
		Map<String, T> map = new HashMap<String, T>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
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
		return map;
	}

}
