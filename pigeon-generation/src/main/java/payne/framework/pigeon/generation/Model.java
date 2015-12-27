package payne.framework.pigeon.generation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import payne.framework.pigeon.generation.annotation.Ignore;
import payne.framework.pigeon.generation.annotation.Name;

import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

public class Model extends Annotated implements Generable {
	private final Class<?> type;
	private final Set<Property> properties;
	private final boolean isEnum;
	private final Map<String, String> mapping;

	public Model(Class<?> type) throws IntrospectionException {
		super(type.isAnnotationPresent(Name.class) ? type.getAnnotation(Name.class).value() : type.getSimpleName(), type.getAnnotations());
		this.type = type;
		this.properties = new LinkedHashSet<Property>();
		this.mapping = new HashMap<String, String>();

		// 适配枚举类型
		if (type.isEnum()) {
			this.isEnum = true;
			for (Object constant : type.getEnumConstants()) {
				Enum<?> _enum = (Enum<?>) constant;
				properties.add(new Property(type, _enum.name(), null));
			}
			return;
		} else {
			this.isEnum = false;
		}

		Set<String> ignores = new HashSet<String>();
		Class<?> clazz = type;
		while (clazz != Object.class && clazz != null) {
			Ignore ignore = clazz.getAnnotation(Ignore.class);
			ignores.addAll(Arrays.asList(ignore != null ? ignore.properties() : new String[] {}));

			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Ignore.class) || Modifier.isTransient(field.getModifiers())) {
					ignores.add(field.getName());
				}
			}

			PropertyDescriptor[] descriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
			for (PropertyDescriptor descriptor : descriptors) {
				if (descriptor.getReadMethod().isAnnotationPresent(Ignore.class)) {
					ignores.add(descriptor.getName());
				}
			}

			clazz = clazz.getSuperclass();
		}

		PropertyDescriptor[] descriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			if (descriptor.getName().equals("class") || ignores.contains(descriptor.getName())) {
				continue;
			}
			Method getter = descriptor.getReadMethod();
			Type _type = getter.getGenericReturnType();
			String _name = getter.isAnnotationPresent(Name.class) ? getter.getAnnotation(Name.class).value() : descriptor.getName();
			Property property = new Property(_type, _name, descriptor.getReadMethod().getAnnotations());
			properties.add(property);
			if (!_name.equals(descriptor.getName())) {
				mapping.put(_name, descriptor.getName());
			}
		}
	}

	public Class<?> getType() {
		return type;
	}

	public Set<Property> getProperties() {
		return properties;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public Map<String, String> getMapping() {
		return mapping;
	}

}
