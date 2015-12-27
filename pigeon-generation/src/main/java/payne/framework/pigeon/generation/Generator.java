package payne.framework.pigeon.generation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.app.VelocityEngine;

import payne.framework.pigeon.generation.exception.GeneratorException;

public abstract class Generator {
	protected final Properties properties;
	protected final VelocityEngine engine;

	protected Generator() throws IOException {
		this("pigeon-generation.properties");
	}

	protected Generator(String pathToProperties) throws IOException {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(pathToProperties);
		if (in == null) {
			throw new FileNotFoundException(pathToProperties);
		}
		Properties temp = new Properties();
		temp.load(in);
		properties = new Properties();
		properties.putAll(temp);
		engine = new VelocityEngine(properties);
	}

	protected Generator(Properties properties) {
		this.properties = properties;
		engine = new VelocityEngine(this.properties);
	}

	public abstract void generate(Interface _interface) throws GeneratorException, IOException;

	public abstract void generate(Model model) throws GeneratorException, IOException;

	public Object create(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return Class.forName(className).newInstance();
	}

	public String concatenate(Collection<?> collection, String separator) {
		StringBuilder builder = new StringBuilder();
		Iterator<?> iterator = collection.iterator();
		while (iterator.hasNext()) {
			builder.append(iterator.next().toString());
			if (iterator.hasNext()) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	public abstract Set<Class<?>> imports(Type type);

	public abstract String getName(Type type);

	public Properties getProperties() {
		return properties;
	}

	public VelocityEngine getEngine() {
		return engine;
	}

}
