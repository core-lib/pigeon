package payne.framework.pigeon.core.factory.bean;

import java.io.IOException;
import java.util.Properties;

public class PrototypeBeanFactory extends ConfigurationBeanFactory {

	public PrototypeBeanFactory() throws IOException {
		super();
	}

	public PrototypeBeanFactory(ClassLoader classLoader, String... paths) throws IOException {
		super(classLoader, paths);
	}

	public PrototypeBeanFactory(ClassLoader classLoader) throws IOException {
		super(classLoader);
	}

	public PrototypeBeanFactory(Properties properties, ClassLoader classLoader) {
		super(properties, classLoader);
	}

	public PrototypeBeanFactory(Properties properties) {
		super(properties);
	}

	public PrototypeBeanFactory(String... paths) throws IOException {
		super(paths);
	}

}
