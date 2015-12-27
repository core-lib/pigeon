package payne.framework.pigeon.generation.objc;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import payne.framework.pigeon.core.Pigeons;
import payne.framework.pigeon.generation.Function;
import payne.framework.pigeon.generation.Generation;
import payne.framework.pigeon.generation.Generator;
import payne.framework.pigeon.generation.Interface;
import payne.framework.pigeon.generation.Model;
import payne.framework.pigeon.generation.Parameter;
import payne.framework.pigeon.generation.Property;
import payne.framework.pigeon.generation.annotation.Name;
import payne.framework.pigeon.generation.exception.GeneratorException;
import payne.framework.pigeon.generation.objc.converter.ObjectiveCConverter;

import com.googlecode.openbeans.IntrospectionException;

public class ObjectiveCGenerator extends Generator {
	private final File directory;
	private final String prefix;
	private final Set<ObjectiveCConverter> converters;
	private final Generation apiInterfaceGeneration;
	private final Generation apiImplementationGeneration;
	private final Generation modelInterfaceGeneration;
	private final Generation modelImplementationGeneration;

	public ObjectiveCGenerator(String directory) throws IOException, GeneratorException {
		this(new File(directory));
	}

	public ObjectiveCGenerator(File directory) throws IOException, GeneratorException {
		super();
		try {
			this.directory = directory;
			this.prefix = properties.getProperty("objective-c-prefix");
			this.directory.mkdirs();
			this.apiInterfaceGeneration = createApiInterfaceGeneration();
			this.apiImplementationGeneration = createApiImplementationGeneration();
			this.modelInterfaceGeneration = createModelInterfaceGeneration();
			this.modelImplementationGeneration = createModelImplementationGeneration();
			this.converters = createObjectiveCConverters();
		} catch (Exception e) {
			throw new GeneratorException(e);
		}
	}

	public ObjectiveCGenerator(String pathToProperties, String directory) throws IOException, GeneratorException {
		this(pathToProperties, new File(directory));
	}

	public ObjectiveCGenerator(String pathToProperties, File directory) throws IOException, GeneratorException {
		super(pathToProperties);
		try {
			this.directory = directory;
			this.prefix = properties.getProperty("objective-c-prefix");
			this.directory.mkdirs();
			this.apiInterfaceGeneration = createApiInterfaceGeneration();
			this.apiImplementationGeneration = createApiImplementationGeneration();
			this.modelInterfaceGeneration = createModelInterfaceGeneration();
			this.modelImplementationGeneration = createModelImplementationGeneration();
			this.converters = createObjectiveCConverters();
		} catch (Exception e) {
			throw new GeneratorException(e);
		}
	}

	public ObjectiveCGenerator(Properties properties, String directory) throws IOException, GeneratorException {
		this(properties, new File(directory));
	}

	public ObjectiveCGenerator(Properties properties, File directory) throws IOException, GeneratorException {
		super(properties);
		try {
			this.directory = directory;
			this.prefix = properties.getProperty("objective-c-prefix");
			this.directory.mkdirs();
			this.apiInterfaceGeneration = createApiInterfaceGeneration();
			this.apiImplementationGeneration = createApiImplementationGeneration();
			this.modelInterfaceGeneration = createModelInterfaceGeneration();
			this.modelImplementationGeneration = createModelImplementationGeneration();
			this.converters = createObjectiveCConverters();
		} catch (Exception e) {
			throw new GeneratorException(e);
		}
	}

	protected Generation createApiInterfaceGeneration() {
		Template template = engine.getTemplate(properties.getProperty("objective-c-interface"));
		String prefix = this.prefix;
		String suffix = properties.getProperty("objective-c-interface-suffix");
		return new Generation(template, prefix, suffix);
	}

	protected Generation createApiImplementationGeneration() {
		Template template = engine.getTemplate(properties.getProperty("objective-c-implementation"));
		String prefix = this.prefix;
		String suffix = properties.getProperty("objective-c-implementation-suffix");
		return new Generation(template, prefix, suffix);
	}

	protected Generation createModelInterfaceGeneration() {
		Template template = engine.getTemplate(properties.getProperty("objective-c-model-interface"));
		String prefix = this.prefix;
		String suffix = properties.getProperty("objective-c-interface-suffix");
		return new Generation(template, prefix, suffix);
	}

	protected Generation createModelImplementationGeneration() {
		Template template = engine.getTemplate(properties.getProperty("objective-c-model-implementation"));
		String prefix = this.prefix;
		String suffix = properties.getProperty("objective-c-implementation-suffix");
		return new Generation(template, prefix, suffix);
	}

	protected LinkedHashSet<ObjectiveCConverter> createObjectiveCConverters() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String prefix = "objective-c-converter-";
		TreeMap<Float, ObjectiveCConverter> converters = new TreeMap<Float, ObjectiveCConverter>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith(prefix)) {
				Float index = Float.valueOf(key.substring(prefix.length()));
				String[] spliteds = properties.getProperty(key).split(":");
				Class<?> _class = classLoader.loadClass(spliteds[0]);
				ObjectiveCConverter converter = (ObjectiveCConverter) _class.newInstance();
				converter.setGenerator(this);
				String[] attributes = spliteds.length > 1 ? spliteds[1].replaceAll("[()]", "").split(",") : new String[] {};
				converter.setAttributes(new LinkedHashSet<String>(Arrays.asList(attributes)));
				converters.put(index, converter);
			}
		}
		return new LinkedHashSet<ObjectiveCConverter>(converters.values());
	}

	@Override
	public void generate(Interface _interface) throws GeneratorException, IOException {
		if (!Pigeons.isOpenableInterface(_interface.getType())) {
			throw new GeneratorException(_interface.getType() + " is not an openable interface");
		}

		Set<Class<?>> imports = new HashSet<Class<?>>();
		for (Function function : _interface.getFunctions()) {
			imports.addAll(this.imports(function.getMethod().getGenericReturnType()));
			for (Parameter parameter : function.getParameters()) {
				imports.addAll(this.imports(parameter.getType()));
			}
		}

		VelocityContext context = new VelocityContext();
		context.put("properties", properties);
		context.put("converter", this);
		context.put("interface", _interface);
		context.put("functions", _interface.getFunctions());
		context.put("imports", imports);

		apiInterfaceGeneration.generate(context, directory, _interface);
		apiImplementationGeneration.generate(context, directory, _interface);
	}

	public void generate(Model model) throws GeneratorException, IOException {
		Set<Class<?>> imports = new HashSet<Class<?>>();
		Map<String, Type> arrays = new HashMap<String, Type>();
		for (Property property : model.getProperties()) {
			imports.addAll(this.imports(property.getType()));
			Type type = property.getType();
			if (type instanceof Class<?>) {
				Class<?> clazz = (Class<?>) type;
				if (clazz.isArray() && generable(clazz.getComponentType())) {
					arrays.put(property.getName(), clazz.getComponentType());
				}
			} else if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> clazz = (Class<?>) parameterizedType.getRawType();
				if (Collection.class.isAssignableFrom(clazz) && generable(parameterizedType.getActualTypeArguments()[0])) {
					arrays.put(property.getName(), parameterizedType.getActualTypeArguments()[0]);
				}
			}
		}
		imports.remove(model.getType());

		VelocityContext context = new VelocityContext();

		context.put("properties", properties);
		context.put("converter", this);
		context.put("model", model);
		context.put("imports", imports);
		context.put("arrays", arrays);

		if (!modelInterfaceGeneration.exists(directory, model)) {
			modelInterfaceGeneration.generate(context, directory, model);
		}

		if (!modelImplementationGeneration.exists(directory, model) && !model.isEnum()) {
			modelImplementationGeneration.generate(context, directory, model);
		}
	}

	/**
	 * 适配泛型类型的java与objective-c类型转换
	 * 
	 * @param type
	 *            泛型类型
	 * @return 对应objective-c 类型
	 * @throws IntrospectionException
	 */
	public String convert(Type type) throws GeneratorException, IOException, IntrospectionException {
		for (ObjectiveCConverter converter : converters) {
			if (converter.supports(type)) {
				if (converter.generable(type)) {
					if (type instanceof Class<?>) {
						this.generate(new Model((Class<?>) type));
					} else if (type instanceof ParameterizedType) {
						this.generate(new Model((Class<?>) ((ParameterizedType) type).getRawType()));
					}
					return prefix + converter.convert(type);
				} else {
					return converter.convert(type);
				}
			}
		}
		throw new IllegalArgumentException("can not convert " + type + "by using " + this);
	}

	/**
	 * 适配泛型类型的参数
	 * 
	 * @param type
	 *            反省类型
	 * @param name
	 *            参数类型
	 * @return 将基本类型转换成Object类型 或 将object类型进行判断 的objective-c代码
	 */
	public String convert(Type type, String name) {
		for (ObjectiveCConverter converter : converters) {
			if (converter.supports(type)) {
				return converter.convert(type, name);
			}
		}
		throw new IllegalArgumentException("can not convert " + type + " named " + name + "by using " + this);
	}

	/**
	 * 将注解递归地转换成dictionary 并且忽略(hashCode|equals|toString|annotationType)方法
	 * 
	 * @param annotation
	 *            注解
	 * @return 对应的dictionary
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public String convert(Annotation annotation) {
		Method[] methods = annotation.annotationType().getDeclaredMethods();
		Map<String, Object> map = new HashMap<String, Object>();
		for (Method method : methods) {
			Object value = null;
			try {
				value = method.invoke(annotation);
			} catch (Exception e) {
				continue;
			}
			map.put(method.getName(), value);
		}
		if (map.isEmpty()) {
			return "@{}";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("@{");
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			builder.append("@\"" + key + "\":");
			builder.append(convert(value));
			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		builder.append("}");
		return builder.toString();
	}

	private String convert(Object value) {
		StringBuilder builder = new StringBuilder();
		if (value.getClass().isArray()) {
			builder.append("@[");
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++) {
				builder.append(convert(Array.get(value, i)));
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("]");
		} else if (value instanceof String) {
			builder.append("@\"" + value + "\"");
		} else if (value instanceof Class<?>) {
			builder.append("@\"" + value + "\"");
		} else if (value.getClass().isEnum()) {
			builder.append("@\"" + value + "\"");
		} else if (value instanceof Annotation) {
			builder.append(convert((Annotation) value));
		} else {
			builder.append("@(" + value + ")");
		}
		return builder.toString();
	}

	public boolean generable(Type type) {
		for (ObjectiveCConverter converter : converters) {
			if (converter.supports(type)) {
				return converter.generable(type);
			}
		}
		throw new IllegalArgumentException("can not generable " + type + "by using " + this);
	}

	public Set<String> getAttributes(Type type) {
		for (ObjectiveCConverter converter : converters) {
			if (converter.supports(type)) {
				return converter.getAttributes();
			}
		}
		throw new IllegalArgumentException("can not getAttributes of " + type + "by using " + this);
	}

	public Set<Class<?>> imports(Type type) {
		for (ObjectiveCConverter converter : converters) {
			if (converter.supports(type)) {
				return converter.imports(this, type);
			}
		}
		throw new IllegalArgumentException("can not imports of " + type + "by using " + this);
	}

	public String getName(Type type) {
		Class<?> clazz = null;
		String name = null;
		if (type instanceof Class<?>) {
			clazz = (Class<?>) type;
			name = clazz.isAnnotationPresent(Name.class) ? clazz.getAnnotation(Name.class).value() : clazz.getSimpleName();
		} else if (type instanceof ParameterizedType) {
			clazz = (Class<?>) ((ParameterizedType) type).getRawType();
			name = clazz.isAnnotationPresent(Name.class) ? clazz.getAnnotation(Name.class).value() : clazz.getSimpleName();
		}
		if (clazz == null || name == null) {
			throw new IllegalArgumentException("can not getName " + type + "by using " + this);
		}

		if (generable(clazz)) {
			try {
				generate(new Model(clazz));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return prefix + name;
	}

	public File getDirectory() {
		return directory;
	}

	public String getPrefix() {
		return prefix;
	}

	public Set<ObjectiveCConverter> getConverters() {
		return converters;
	}

	public Generation getApiInterfaceGeneration() {
		return apiInterfaceGeneration;
	}

	public Generation getApiImplementationGeneration() {
		return apiImplementationGeneration;
	}

	public Generation getModelInterfaceGeneration() {
		return modelInterfaceGeneration;
	}

	public Generation getModelImplementationGeneration() {
		return modelImplementationGeneration;
	}

}
