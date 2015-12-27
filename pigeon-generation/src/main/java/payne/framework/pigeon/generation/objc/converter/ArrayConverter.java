package payne.framework.pigeon.generation.objc.converter;

import java.util.Set;

import payne.framework.pigeon.generation.Generator;

public class ArrayConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isArray();
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (supports(clazz)) {
			return "NSArray *";
		}
		return super.convert(clazz);
	}

	@Override
	protected String convert(Class<?> clazz, String name) {
		if (generator.generable(clazz)) {
			String typeName = generator.getName(clazz.getComponentType());
			return name + " ? [" + typeName + " keyValuesArrayWithObjectArray:" + name + "] : " + "[NSNull null]";
		}
		return name + " ? " + name + " : " + "[NSNull null]";
	}

	@Override
	protected Set<Class<?>> imports(Generator fg, Class<?> clazz) {
		return fg.imports(clazz.getComponentType());
	}
}
