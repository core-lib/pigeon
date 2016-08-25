package payne.framework.pigeon.generation.objc.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import payne.framework.pigeon.generation.Generator;

public class MapConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean supports(ParameterizedType type) {
		return supports(type.getRawType());
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (supports(clazz)) {
			return "NSDictionary *";
		}
		return super.convert(clazz);
	}

	@Override
	protected String convert(ParameterizedType type) {
		if (supports(type)) {
			return "NSDictionary *";
		}
		return super.convert(type);
	}
	
	@Override
	protected String convert(Class<?> clazz, String name) {
		return name + " ? " + name + " : " + "[NSNull null]";
	}

	@Override
	protected String convert(ParameterizedType type, String name) {
		Type clazz = type.getActualTypeArguments()[1];
		if (generator.generable(clazz)) {
			String typeName = generator.getName(clazz);
			return name + " ? [" + typeName + " mj_keyValuesDictionaryWithObjectDictionary:" + name + "] : " + "[NSNull null]";
		}
		return name + " ? " + name + " : " + "[NSNull null]";
	}

	@Override
	protected Set<Class<?>> imports(Generator fg, ParameterizedType type) {
		return fg.imports(type.getActualTypeArguments()[1]);
	}

}
