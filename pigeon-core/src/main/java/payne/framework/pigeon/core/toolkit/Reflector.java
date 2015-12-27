package payne.framework.pigeon.core.toolkit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

/**
 * 反射工具
 * 
 * 
 * @author Change 2014年5月15日
 * 
 */
public class Reflector {

	public static Object getFieldValueByName(Object target, String name) throws IllegalArgumentException, IllegalAccessException {
		Field field = getFieldByName(target.getClass(), name);
		field.setAccessible(true);
		return field.get(target);
	}

	/**
	 * 获取字段,如果在该类找不到会从祖先类去找
	 * 
	 * @param clazz
	 *            类
	 * @param name
	 *            字段名
	 * @return 字段 如果找不到将返回null
	 */
	public static Field getFieldByName(Class<?> clazz, String name) {
		Field field = null;
		while (clazz != null && field == null) {
			try {
				field = clazz.getDeclaredField(name);
			} catch (Exception e) {
				clazz = clazz.getSuperclass();
			}
		}
		return field;
	}

	/**
	 * 得到指定注解的所有fields 包括所有超类的
	 * 
	 * @param _class
	 *            类
	 * @param annotation
	 *            注解类型
	 * @return 本身类以及所有超类的标注了该注解的所有fields
	 */
	public static Set<Field> getAnnotatedFields(Class<?> _class, Class<? extends Annotation> annotation) {
		Set<Field> result = new HashSet<Field>();

		while (_class != Object.class) {
			Field[] fields = _class.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(annotation)) {
					result.add(field);
				}
			}
			_class = _class.getSuperclass();
		}

		return result;
	}

	public static Set<Field> getAllDeclaredFields(Class<?> _class) {
		Set<Field> fields = new HashSet<Field>();

		while (_class != Object.class) {
			fields.addAll(Arrays.asList(_class.getDeclaredFields()));
			_class = _class.getSuperclass();
		}

		return fields;
	}

	/**
	 * 得到指定注解的所有setters 包括所有超类的
	 * 
	 * @param _class
	 *            类
	 * @param annotation
	 *            注解类型
	 * @return 本身类以及所有超类的标注了该注解的所有setters
	 */
	public static Set<Method> getAnnotatedSetters(Class<?> _class, Class<? extends Annotation> annotation) {
		Set<Method> result = new HashSet<Method>();

		try {
			while (_class != Object.class) {
				PropertyDescriptor[] descriptors = Introspector.getBeanInfo(_class).getPropertyDescriptors();
				for (PropertyDescriptor descriptor : descriptors) {
					if (descriptor.getName().equals("class")) {
						continue;
					}
					Method setter = descriptor.getWriteMethod();
					if (setter != null && setter.isAnnotationPresent(annotation)) {
						result.add(setter);
					}
				}
				_class = _class.getSuperclass();
			}
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	/**
	 * 得到指定注解的所有getters 包括所有超类的
	 * 
	 * @param _class
	 *            类
	 * @param annotation
	 *            注解类型
	 * @return 本身类以及所有超类的标注了该注解的所有getters
	 */
	public static Set<Method> getAnnotatedGetters(Class<?> _class, Class<? extends Annotation> annotation) {
		Set<Method> result = new HashSet<Method>();

		try {
			while (_class != Object.class) {
				PropertyDescriptor[] descriptors = Introspector.getBeanInfo(_class).getPropertyDescriptors();
				for (PropertyDescriptor descriptor : descriptors) {
					if (descriptor.getName().equals("class")) {
						continue;
					}
					Method getter = descriptor.getReadMethod();
					if (getter != null && getter.isAnnotationPresent(annotation)) {
						result.add(getter);
					}
				}
				_class = _class.getSuperclass();
			}
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

}
