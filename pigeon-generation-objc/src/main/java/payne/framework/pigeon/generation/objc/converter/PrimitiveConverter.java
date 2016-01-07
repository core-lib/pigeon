package payne.framework.pigeon.generation.objc.converter;

import java.util.HashMap;
import java.util.Map;

public class PrimitiveConverter extends ObjectiveCConverter {
	private static final Map<Class<?>, String> PRIMITIVES = new HashMap<Class<?>, String>();

	static {
		PRIMITIVES.put(boolean.class, "bool");
		PRIMITIVES.put(byte.class, "int");
		PRIMITIVES.put(char.class, "char");
		PRIMITIVES.put(short.class, "short");
		PRIMITIVES.put(int.class, "int");
		PRIMITIVES.put(long.class, "long");
		PRIMITIVES.put(float.class, "float");
		PRIMITIVES.put(double.class, "double");
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return PRIMITIVES.containsKey(clazz);
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (supports(clazz)) {
			return PRIMITIVES.get(clazz);
		}
		return super.convert(clazz);
	}

	@Override
	public String convert(Class<?> clazz, String name) {
		if (supports(clazz)) {
			return "@(" + name + ")";
		}
		throw new IllegalArgumentException("can not convert class " + clazz + " by using converter " + this);
	}

}
