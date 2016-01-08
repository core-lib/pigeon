package payne.framework.pigeon.core.factory.bean;

import java.util.Properties;

public class PrototypeBeanFactory extends ConfigurableBeanFactory {

	public PrototypeBeanFactory() {
		super();
	}

	public PrototypeBeanFactory(ClassLoader classLoader, String... configLocations) {
		super(classLoader, configLocations);
	}

	public PrototypeBeanFactory(ClassLoader classLoader) {
		super(classLoader);
	}

	public PrototypeBeanFactory(Properties properties, ClassLoader classLoader) {
		super(properties, classLoader);
	}

	public PrototypeBeanFactory(Properties properties) {
		super(properties);
	}

	public PrototypeBeanFactory(String... configLocations) {
		super(configLocations);
	}

}
