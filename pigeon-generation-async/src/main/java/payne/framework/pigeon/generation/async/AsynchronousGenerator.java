package payne.framework.pigeon.generation.async;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import payne.framework.pigeon.core.*;
import payne.framework.pigeon.core.annotation.Correspond;
import payne.framework.pigeon.generation.Function;
import payne.framework.pigeon.generation.Generation;
import payne.framework.pigeon.generation.Generator;
import payne.framework.pigeon.generation.Interface;
import payne.framework.pigeon.generation.Model;
import payne.framework.pigeon.generation.exception.GeneratorException;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年8月4日 上午10:46:22
 *
 * @version 1.0.0
 */
public class AsynchronousGenerator extends Generator {
	private final File directory;

	public AsynchronousGenerator(String directory) throws IOException {
		this(new File(directory));
	}

	public AsynchronousGenerator(File directory) throws IOException {
		super("pigeon-generation-async.properties");
		this.directory = directory;
	}

	public AsynchronousGenerator(Properties properties, String directory) throws IOException {
		this(properties, new File(directory));
	}

	public AsynchronousGenerator(Properties properties, File directory) throws IOException {
		super(properties);
		this.directory = directory;
	}

	public AsynchronousGenerator(String pathToProperties, String directory) throws IOException {
		this(pathToProperties, new File(directory));
	}

	public AsynchronousGenerator(String pathToProperties, File directory) throws IOException {
		super(pathToProperties);
		this.directory = directory;
	}

	@Override
	public void generate(Interface _interface) throws GeneratorException, IOException {
		if (!Pigeons.isOpenableInterface(_interface.getType())) {
			throw new GeneratorException(_interface.getType() + " is not an openable interface");
		}

		VelocityContext context = new VelocityContext();
		context.put("properties", properties);
		context.put("converter", this);
		context.put("interface", _interface);
		context.put("functions", _interface.getFunctions());
		context.put("imports", imports(_interface));

		String prefix = properties.getProperty("prefix");
		String suffix = properties.getProperty("suffix");
		Template template = engine.getTemplate(properties.getProperty("interface"));
		Generation generation = new Generation(template, prefix, suffix);
		generation.generate(context, directory, _interface);
	}

	public Set<Class<?>> imports(Interface interfase) {
		Set<Class<?>> imports = new TreeSet<Class<?>>(new ClassNameComparator());
		imports.add(Correspond.class);
		imports.add(interfase.getType());
		imports.add(Callback.class);
		imports.add(OnCompleted.class);
		imports.add(OnFail.class);
		imports.add(OnSuccess.class);
		// 导入注解的依赖
		for (Annotation annotation : interfase.getAnnotations()) {
			imports.addAll(imports(annotation));
		}
		// 导入泛型参数的依赖
		TypeVariable<?>[] typeParameters = interfase.getType().getTypeParameters();
		for (int i = 0; typeParameters != null && i < typeParameters.length; i++) {
			imports.addAll(imports(typeParameters[i], true));
		}
		// 导入方法的依赖
		for (Function function : interfase.getFunctions()) {
			imports.addAll(imports(function));
		}
		// 去掉java的常用包依赖和去掉和自身同包的依赖
		Iterator<Class<?>> iterator = imports.iterator();
		while (iterator.hasNext()) {
			Class<?> clazz = iterator.next();
			if (clazz.isPrimitive()) {
				iterator.remove();
				continue;
			}
			if (clazz == null || clazz.getPackage() == null || "java.lang".equals(clazz.getPackage().getName())) {
				iterator.remove();
			}
		}
		return imports;
	}

	@Override
	public Set<Class<?>> imports(Type type) {
		return new HashSet<Class<?>>();
	}

	@Override
	public String getName(Type type) {
		return null;
	}

	public Set<Class<?>> imports(Function function) {
		Set<Class<?>> imports = new HashSet<Class<?>>();
		// 添加注解的依赖
		for (Annotation annotation : function.getAnnotations()) {
			imports.addAll(imports(annotation));
		}
		// 添加返回值类型的依赖
		imports.addAll(imports(function.getMethod().getGenericReturnType(), true));
		// 添加参数的依赖
		for (Type type : function.getMethod().getGenericParameterTypes()) {
			imports.addAll(imports(type, false));
		}
		// 添加参数注解的依赖
		for (Annotation[] annotations : function.getMethod().getParameterAnnotations()) {
			for (Annotation annotation : annotations) {
				imports.addAll(imports(annotation));
			}
		}
		return imports;
	}

	public Set<Class<?>> imports(Annotation annotation) {
		Set<Class<?>> imports = new HashSet<Class<?>>();
		// 添加对自身的依赖
		imports.add(annotation.annotationType());
		for (Method method : annotation.annotationType().getDeclaredMethods()) {
			try {
				Object value = method.invoke(annotation);
				// 如果和默认值相等是不会声明的所以可以忽略
				if (value.equals(method.getDefaultValue())) {
					continue;
				}
				// 如果是数组
				if (value.getClass().isArray()) {
					int length = Array.getLength(value);
					for (int i = 0; i < length; i++) {
						Object element = Array.get(value, i);
						if (element instanceof Class<?>) {
							imports.add((Class<?>) element);
						} else if (element instanceof Enum<?>) {
							imports.add(element.getClass());
						} else if (element instanceof Annotation) {
							imports.addAll(imports((Annotation) element));
						} else {
							imports.add(element.getClass());
						}
					}
				}
				// 添加Class类型的依赖
				else if (value instanceof Class<?>) {
					imports.add((Class<?>) value);
				}
				// 添加枚举类型的依赖
				else if (value instanceof Enum<?>) {
					imports.add(((Enum<?>) value).getClass());
				}
				// 添加注解类型的依赖
				else if (value instanceof Annotation) {
					imports.addAll(imports((Annotation) value));
				}
			} catch (Exception e) {
				continue;
			}
		}
		return imports;
	}

	/**
	 * 递归分析java所有类型的依赖
	 * 
	 * @param type
	 * @param bound
	 * @return
	 */
	public Set<Class<?>> imports(Type type, boolean bound) {
		Set<Class<?>> imports = new HashSet<Class<?>>();
		if (type == null) {
			throw new NullPointerException("parameter named type must not be null");
		}
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			imports.add(clazz);
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			imports.addAll(imports(rawType, bound));
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			for (int i = 0; i < actualTypeArguments.length; i++) {
				imports.addAll(imports(actualTypeArguments[i], bound));
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			imports.addAll(imports(genericArrayType.getGenericComponentType(), bound));
		} else if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			if (bound && typeVariable.getBounds() != null && typeVariable.getBounds().length > 0) {
				imports.addAll(imports(typeVariable.getBounds()[0], bound));
			}
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type[] lowerBounds = wildcardType.getLowerBounds();
			Type[] upperBounds = wildcardType.getUpperBounds();
			if (lowerBounds != null && lowerBounds.length > 0) {
				imports.addAll(imports(lowerBounds[0], bound));
			} else if (upperBounds != null && upperBounds.length > 0) {
				imports.addAll(imports(upperBounds[0], bound));
			}
		}
		return imports;
	}

	/**
	 * 解析泛型参数列表的声明
	 * 
	 * @param typeParameters
	 *            泛型参数列表
	 * @return 泛型参数列表的声明
	 */
	public String resolve(TypeVariable<?>[] typeParameters) {
		StringBuilder builder = new StringBuilder();
		if (typeParameters != null && typeParameters.length > 0) {
			builder.append("<");
			for (int i = 0; i < typeParameters.length; i++) {
				builder.append(toString(typeParameters[i], true));
				if (i < typeParameters.length - 1) {
					builder.append(", ");
				}
			}
			builder.append(">");
		}
		return builder.toString();
	}

	public String toString(Type type) {
		return toString(type, false);
	}

	public String toString(Type type, boolean bound) {
		if (type == null) {
			throw new NullPointerException("parameter named type must not be null");
		}
		if (type == Void.TYPE) {
			return Void.class.getSimpleName();
		}
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			return clazz.getSimpleName();
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			StringBuilder builder = new StringBuilder();
			Type rawType = parameterizedType.getRawType();
			builder.append(toString(rawType, bound));
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			builder.append("<");
			for (int i = 0; i < actualTypeArguments.length; i++) {
				builder.append(toString(actualTypeArguments[i], bound));
				if (i < actualTypeArguments.length - 1) {
					builder.append(", ");
				}
			}
			builder.append(">");
			return builder.toString();
		}
		if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			return toString(genericArrayType.getGenericComponentType(), bound) + "[]";
		}
		if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			if (bound && typeVariable.getBounds() != null && typeVariable.getBounds().length > 0) {
				return typeVariable.getName() + " extends " + toString(typeVariable.getBounds()[0], bound);
			}
			return typeVariable.getName();
		}
		if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type[] lowerBounds = wildcardType.getLowerBounds();
			Type[] upperBounds = wildcardType.getUpperBounds();
			if (lowerBounds != null && lowerBounds.length > 0) {
				return "? super " + toString(lowerBounds[0], bound);
			}
			if (upperBounds != null && upperBounds.length > 0) {
				return "? extends " + toString(upperBounds[0], bound);
			}
		}
		throw new IllegalArgumentException(type.toString());
	}

	public String toString(Annotation annotation) {
		Method[] methods = annotation.annotationType().getDeclaredMethods();
		Map<String, Object> map = new HashMap<String, Object>();
		for (Method method : methods) {
			Object value = null;
			try {
				value = method.invoke(annotation);
				if (value == method.getDefaultValue() || value.equals(method.getDefaultValue())) {
					continue;
				}
			} catch (Exception e) {
				return null;
			}
			map.put(method.getName(), value);
		}
		if (map.isEmpty()) {
			return "@" + annotation.annotationType().getSimpleName();
		}
		StringBuilder builder = new StringBuilder();
		builder.append("@").append(annotation.annotationType().getSimpleName());
		builder.append("(");
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			builder.append(key).append(" = ");
			builder.append(toString(value));
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append(")");
		return builder.toString();
	}

	private String toString(Object value) {
		StringBuilder builder = new StringBuilder();
		if (value.getClass().isArray()) {
			builder.append("{");
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++) {
				builder.append(toString(Array.get(value, i)));
				if (i < length - 1) {
					builder.append(", ");
				}
			}
			builder.append("}");
		} else if (value instanceof String) {
			builder.append("\"" + value + "\"");
		} else if (value instanceof Class<?>) {
			Class<?> clazz = (Class<?>) value;
			builder.append(clazz.getSimpleName()).append(".class");
		} else if (value instanceof Enum<?>) {
			Enum<?> _enum = (Enum<?>) value;
			builder.append(_enum.getClass().getSimpleName()).append(".").append(_enum.name());
		} else if (value instanceof Annotation) {
			builder.append(toString((Annotation) value));
		} else {
			String type = "";
			if (value instanceof Boolean) {
				type = "(boolean)";
			} else if (value instanceof Byte) {
				type = "(byte)";
			} else if (value instanceof Short) {
				type = "(short)";
			} else if (value instanceof Character) {
				type = "(char)";
			} else if (value instanceof Integer) {
				type = "(int)";
			} else if (value instanceof Float) {
				type = "(float)";
			} else if (value instanceof Long) {
				type = "(long)";
			} else if (value instanceof Double) {
				type = "(double)";
			}
			builder.append(type).append(value);
		}
		return builder.toString();
	}

	@Override
	public void generate(Model model) throws GeneratorException, IOException {
		throw new UnsupportedOperationException();
	}

}
