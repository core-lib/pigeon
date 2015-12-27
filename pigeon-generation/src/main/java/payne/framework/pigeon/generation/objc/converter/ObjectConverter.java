package payne.framework.pigeon.generation.objc.converter;


public class ObjectConverter extends ObjectiveCConverter {

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz == Object.class;
	}

	@Override
	protected String convert(Class<?> clazz) {
		if (supports(clazz)) {
			return "NSObject *";
		}
		return super.convert(clazz);
	}

	@Override
	protected String convert(Class<?> clazz, String name) {
		return name + " ? [[NSDictionary alloc] init] : [NSNull null]";
	}
	
}
