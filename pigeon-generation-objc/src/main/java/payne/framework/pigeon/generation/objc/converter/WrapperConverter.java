package payne.framework.pigeon.generation.objc.converter;

import java.util.HashSet;
import java.util.Set;

public class WrapperConverter extends ObjectiveCConverter {
	private static final Set<Class<?>> WRAPPERS = new HashSet<Class<?>>();

	static {
		WRAPPERS.add(Boolean.class);
		WRAPPERS.add(Byte.class);
		WRAPPERS.add(Character.class);
		WRAPPERS.add(Short.class);
		WRAPPERS.add(Integer.class);
		WRAPPERS.add(Long.class);
		WRAPPERS.add(Float.class);
		WRAPPERS.add(Double.class);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return WRAPPERS.contains(clazz);
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (supports(clazz)) {
			return "NSNumber *";
		}
		return super.convert(clazz);
	}

}
