package payne.framework.pigeon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import payne.framework.pigeon.core.annotation.Close;
import payne.framework.pigeon.core.annotation.Intercept;
import payne.framework.pigeon.core.annotation.Open;
import payne.framework.pigeon.core.annotation.Process;
import payne.framework.pigeon.core.exception.BeanInitializeException;
import payne.framework.pigeon.core.processing.Procedure;
import payne.framework.pigeon.core.processing.Step;
import payne.framework.pigeon.core.toolkit.Collections;

/**
 * 工具类
 * 
 * @author yangchangpei
 * 
 */
public abstract class Pigeons {

	public static Set<String> getTransientProperties(Class<?> clazz) {
		Set<String> ignores = new HashSet<String>();

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isTransient(field.getModifiers())) {
				ignores.add(field.getName());
			}
		}

		if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
			ignores.addAll(getTransientProperties(clazz.getSuperclass()));
		}

		return ignores;
	}

	/**
	 * 判断类型是否为可开放的接口,当且仅当类型是接口而且可开放时返回true 否则返回false
	 * 
	 * @param clazz
	 *            指定类型
	 * @return 当且仅当类型是接口而且可开放时返回true 否则返回false
	 */
	public static boolean isOpenableInterface(Class<?> clazz) {
		return clazz.isInterface() && isOpenableClass(clazz);
	}

	/**
	 * 判断类型或接口是可开放的类型,方法将会以递归的方式搜索本类和祖先类的接口和接口继承的接口进行判断,只要有一个接口标注了{@link Open} 那么立即返回true,否则返回false
	 * 
	 * @param clazz
	 *            类型或接口
	 * @return 只要寻找到一个接口标注了{@link Open} 那么返回:true 否则返回:false
	 */
	public static boolean isOpenableClass(Class<?> clazz) {
		// 类型不能是annotation 或 enum
		if (clazz.isAnnotation() || clazz.isEnum()) {
			return false;
		}
		if (clazz.isInterface()) {
			if (clazz.isAnnotationPresent(Open.class)) {
				return true;
			}
			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> _interface : interfaces) {
				if (isOpenableClass(_interface)) {
					return true;
				}
			}
			return false;
		} else {
			if (clazz == Object.class) {
				return false;
			}
			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> interfase : interfaces) {
				if (isOpenableClass(interfase)) {
					return true;
				}
			}
			return isOpenableClass(clazz.getSuperclass());
		}
	}

	/**
	 * 判断方法是否为发放方法
	 * 
	 * @param method
	 *            方法
	 * @return true:如果开放 false:否则
	 */
	public static boolean isOpenableMethod(Method method) {
		if (!method.getDeclaringClass().isInterface()) {
			return false;
		}
		Open open = method.getDeclaringClass().getAnnotation(Open.class);
		return ((open != null && open.all()) || method.isAnnotationPresent(Open.class)) && !method.isAnnotationPresent(Close.class);
	}

	public static String getOpenPath(Object object) {
		if (object instanceof Class<?>) {
			return getOpenPath((Class<?>) object);
		}
		if (object instanceof Method) {
			return getOpenPath((Method) object);
		}
		if (object instanceof String) {
			return getOpenPath((String) object);
		}
		Open open = object.getClass().getAnnotation(Open.class);
		return open == null || open.value().trim().equals("") ? "/" + object.getClass().getSimpleName() : ("/" + open.value().trim()).replaceAll("/+", "/");
	}

	public static String getOpenPath(String string) {
		return "/" + Collections.concatenate(string.trim().split("/+"), "/", "");
	}

	public static String getOpenPath(Class<?> interfase) {
		if (!interfase.isInterface() || !interfase.isAnnotationPresent(Open.class)) {
			throw new IllegalArgumentException(interfase + " is not an openable interfase");
		}
		Open open = interfase.getAnnotation(Open.class);
		return open.value().trim().equals("") ? "/" + interfase.getSimpleName() : ("/" + open.value().trim()).replaceAll("/+", "/");
	}

	public static String getOpenPath(Method method) {
		Open open = method.getAnnotation(Open.class);
		String path = open == null || open.value().trim().equals("") ? method.getName() : open.value().trim();
		return ("/" + path).replaceAll("/+", "/");
	}

	/**
	 * 获取类型实现了的所有可开放接口集合,需要注意的是方法实现递归式搜索,将会获取所有该类型实现了的可开放接口
	 * 
	 * @param clazz
	 *            类型
	 * @return 实现的所有可开放接口类型集合
	 */
	public static Set<Class<?>> getOpenableInterfaces(Class<?> clazz) throws IllegalArgumentException {
		Set<Class<?>> openables = new HashSet<Class<?>>();
		if (clazz.isAnnotation() || clazz.isEnum()) {
			throw new IllegalArgumentException();
		}
		if (clazz.isInterface()) {
			if (clazz.isAnnotationPresent(Open.class)) {
				openables.add(clazz);
			}
			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> _interface : interfaces) {
				openables.addAll(getOpenableInterfaces(_interface));
			}
			return openables;
		} else {
			if (clazz == Object.class) {
				return openables;
			}
			Class<?>[] interfaces = clazz.getInterfaces();
			for (Class<?> interfase : interfaces) {
				openables.addAll(getOpenableInterfaces(interfase));
			}
			openables.addAll(getOpenableInterfaces(clazz.getSuperclass()));
			return openables;
		}
	}

	/**
	 * 获取开放接口上的所定义了开放方法,即只包含该接口定义的开放方法,不包含继承的开放接口方法
	 * 
	 * @param interfase
	 *            开放接口
	 * @return 开放接口集合,如果参数不是接口类型或者没有标注{@link Open}注解 返回null,否则返回该接口所定义的开放方法
	 */
	public static Set<Method> getInterfaceDeclaredOpenableMethods(Class<?> interfase) {
		if (!interfase.isInterface() || !interfase.isAnnotationPresent(Open.class)) {
			return null;
		}
		Open open = interfase.getAnnotation(Open.class);
		Set<Method> opens = new HashSet<Method>();
		Method[] methods = interfase.getDeclaredMethods();
		for (Method method : methods) {
			if ((open.all() || method.isAnnotationPresent(Open.class)) && !method.isAnnotationPresent(Close.class)) {
				opens.add(method);
			}
		}
		return opens;
	}

	/**
	 * 获取类的所有开放接口,方法实现递归式搜索
	 * 
	 * @param clazz
	 *            类型或接口(不包括枚举类型和注解类型)
	 * @return 所有开放接口
	 */
	public static Set<Method> getClassAllOpenableMethods(Class<?> clazz) {
		Set<Method> methods = new HashSet<Method>();
		Set<Class<?>> openables = getOpenableInterfaces(clazz);
		for (Class<?> openable : openables) {
			methods.addAll(getInterfaceDeclaredOpenableMethods(openable));
		}
		return methods;
	}

	/**
	 * 获取类型的所有定义拦截器,包括父类的.如果定义了多个将视为一个,而且顺序将从父类再到子类,同一级的拦截器数组顺序将遵从定义的先后, 如果一旦有Intercept注解的inherit属性为false则不再继续继承父类的拦截器
	 * 
	 * @param clazz
	 *            开放接口实现类型
	 * @return 所有定义拦截器
	 */
	public static LinkedHashSet<Class<? extends Interceptor>> getClassAllInterceptors(Class<?> clazz) {
		LinkedList<Class<? extends Interceptor>> classes = new LinkedList<Class<? extends Interceptor>>();
		while (clazz != Object.class) {
			if (!clazz.isAnnotationPresent(Intercept.class)) {
				clazz = clazz.getSuperclass();
				continue;
			}
			Intercept intercept = clazz.getAnnotation(Intercept.class);
			for (int i = intercept.value().length - 1; i >= 0; i--) {
				classes.addFirst(intercept.value()[i]);
			}
			if (!intercept.inherit()) {
				break;
			}
			clazz = clazz.getSuperclass();
		}
		return new LinkedHashSet<Class<? extends Interceptor>>(classes);
	}

	/**
	 * 获取方法上的注解解析器,方法会一起获取方法的所在开放接口上的注解解析器,如果方法与接口上有相同类型的注解则方法上的该注解会覆盖接口上的同类型注解
	 * 
	 * @param method
	 *            开放方法
	 * @return 方法的注解解析器
	 * @throws BeanInitializeException
	 *             如果解析器初始化失败
	 */
	public static TreeMap<Class<? extends Annotation>, Step> getMethodProcessings(Method method) throws BeanInitializeException {
		if (!isOpenableMethod(method)) {
			throw new IllegalArgumentException("method " + method + "is not an openable method");
		}
		TreeMap<Class<? extends Annotation>, Step> processings = new TreeMap<Class<? extends Annotation>, Step>(new ProcessComparator());
		// 获取解析器 如果接口和方法上有同样类型的注解 那么优先采用方法上的
		List<Annotation> annotations = new LinkedList<Annotation>();
		Open open = method.isAnnotationPresent(Open.class) ? method.getAnnotation(Open.class) : method.getDeclaringClass().getAnnotation(Open.class);
		annotations.add(open.work());
		annotations.addAll(Arrays.asList(method.getDeclaringClass().getAnnotations()));
		annotations.addAll(Arrays.asList(method.getAnnotations()));
		for (Annotation annotation : annotations) {
			if (!annotation.annotationType().isAnnotationPresent(Process.class)) {
				continue;
			}
			Process process = annotation.annotationType().getAnnotation(Process.class);
			try {
				@SuppressWarnings("unchecked")
				Procedure<Annotation> procedure = (Procedure<Annotation>) process.procedure().newInstance();
				Step step = new Step(process, annotation, procedure);
				processings.put(annotation.annotationType(), step);
			} catch (Exception e) {
				throw new BeanInitializeException(process.procedure(), null);
			}
		}
		return processings;
	}

	public static final Pattern PATTERN = Pattern.compile("\\{(?:(\\w+)\\:)?(.*?)\\}");

	/**
	 * 验证路径是否符合规则,即匹配表达式"(/\\w+)+"
	 * 
	 * @param path
	 *            路径
	 * @return 如果符合规则:true 否则:false
	 */
	public static boolean isPathValidate(String path) {
		return path.matches(".*");
	}

}
