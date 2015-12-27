package payne.framework.pigeon.generation.objc.converter;

public class StringConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == String.class || clazz == StringBuilder.class || clazz == StringBuffer.class;
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (clazz == String.class) {
			return "NSString *";
		}
		if (clazz == StringBuilder.class || clazz == StringBuffer.class) {
			return "NSMutableString *";
		}
		return super.convert(clazz);
	}

}
