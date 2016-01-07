package payne.framework.pigeon.generation.objc.converter;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

import payne.framework.pigeon.generation.Generator;
import payne.framework.pigeon.generation.annotation.Name;

public class GenericConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(ParameterizedType type) {
		return true;
	}

	@Override
	protected String convert(ParameterizedType type) {
		Class<?> clazz = (Class<?>) type.getRawType();
		return (clazz.isAnnotationPresent(Name.class) ? clazz.getAnnotation(Name.class).value() : clazz.getSimpleName()) + " *";
	}

	@Override
	protected boolean generable(ParameterizedType type) {
		return true;
	}
	
	@Override
	protected Set<Class<?>> imports(Generator fg, ParameterizedType type) {
		Class<?> clazz = (Class<?>) type.getRawType();
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(clazz);
		return classes;
	}

}
