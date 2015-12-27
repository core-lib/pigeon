package payne.framework.pigeon.generation.objc.converter;

import java.io.Serializable;

public class SerializableConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Serializable.class;
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (clazz == Serializable.class) {
			return "NSString *";
		}
		return super.convert(clazz);
	}

}
